package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.ReservationNotifyDto;
import com.reservAR.backreservar.dto.ReservationRequestDto;
import com.reservAR.backreservar.exception.*;
import com.reservAR.backreservar.model.*;
import com.reservAR.backreservar.repository.*;
import com.reservAR.backreservar.service.IReservationService;
import com.reservAR.backreservar.websocket.ReservationNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;

import java.time.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final InventoryRuleRepository inventoryRuleRepository;
    private final TableEntityRepository tableEntityRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ReservationNotifier notifier;

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(()->new ReservationNotFoundException("Reservation not found"));
    }

    @Override
    @Transactional
    public Reservation save(ReservationRequestDto reservation) {
        Restaurant restaurant = restaurantRepository.findById(reservation.restaurantId())
                .orElseThrow(()->new RestaurantNotFoundException("Restaurant not found"));

        User user = (reservation.userId() != null) ?
                userRepository.findById(reservation.userId())
                .orElseThrow(()->new UserNotFoundException("User not found"))
                : getUserContext();

        InventoryRule rule = inventoryRuleRepository.findByRestaurant(restaurant)
                .orElseThrow(()->new RestaurantException("Restaurant has not inventory rule"));

        DayOfWeek day = DayOfWeek.from(reservation.startDate().toLocalDate());
        LocalTime startTime = reservation.startDate().toLocalTime();
        LocalTime endTime = reservation.startDate().toLocalTime().plusMinutes(rule.getDefaultDurationMin());

        boolean open = availabilityRepository.isOpen(restaurant, day, startTime, endTime);
        if (!open) throw new RestaurantException("Restaurant is not open");

        Instant start = reservation.startDate().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = reservation.startDate().plusMinutes(rule.getDefaultDurationMin()).atZone(ZoneId.systemDefault()).toInstant();
        Instant startCheck = start.minus(Duration.ofMinutes(rule.getPrepBufferMin()));
        Instant endCheck = end.plus(Duration.ofMinutes(rule.getCleanupBufferMin()));

        TableEntity table = tableEntityRepository
                .findFreeTablesByRestaurant(reservation.restaurantId(), startCheck, endCheck).stream()
                .filter((t-> t.getMaxCapacity() >= reservation.partySize() && t.getMinCapacity() <= reservation.partySize()))
                .findFirst()
                .orElseThrow(()-> new TableNotFoundException("Table not found"));

        Reservation newReservation = Reservation.builder()
                .restaurant(restaurant)
                .user(user)
                .start(start)
                .end(end)
                .status(Status.BOOKED)
                .tables(List.of(table))
                .build();

        reservationRepository.save(newReservation);

        notifier.notifyRestaurant(newReservation.getRestaurant().getId(),
                newReservation.getStart(),
                new ReservationNotifyDto("CREATED",
                        newReservation.getId(),
                        newReservation.getStatus().name(),
                        newReservation.getStart()));

        return newReservation;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        return reservationRepository.findAllByUser(user);
    }

    @Override
    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()->new ReservationNotFoundException("Reservation not found"));

        if (reservation.getStatus() == (Status.BOOKED)) {
            reservation.setStatus(Status.CANCELLED);
            Reservation reservationCancelled = reservationRepository.save(reservation);
            notifier.notifyRestaurant(
                    reservationCancelled.getRestaurant().getId(),
                    reservationCancelled.getStart(),
                    new ReservationNotifyDto("CANCELLED",
                            reservationCancelled.getRestaurant().getId(),
                            reservationCancelled.getStatus().name(),
                            reservationCancelled.getStart())
                    );
            return reservationCancelled;
        }else {
            throw new ReservationStatusException("Reservation is not booked");
        }
    }

    @Override
    @Transactional
    public Reservation completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()->new ReservationNotFoundException("Reservation not found"));

        if (reservation.getStatus() == (Status.BOOKED)) {
            reservation.setStatus(Status.COMPLETED);
            Reservation reservationCompleted = reservationRepository.save(reservation);
            notifier.notifyRestaurant(
                    reservationCompleted.getRestaurant().getId(),
                    reservationCompleted.getStart(),
                    new ReservationNotifyDto("COMPLETED",
                            reservationCompleted.getRestaurant().getId(),
                            reservationCompleted.getStatus().name(),
                            reservationCompleted.getStart())
            );

            return reservationCompleted;
        }else {
            throw new ReservationStatusException("Reservation is not booked");
        }
    }


    @Override
    @Transactional
    @Scheduled(cron = "0 */5 * * * *", zone = "America/Argentina/Buenos_Aires")
    public void expiredReservation() {
        ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");

        LocalDate today = LocalDate.now(zone);
        LocalTime nowTime = LocalTime.now(zone);
        DayOfWeek todayDay = today.getDayOfWeek();

        List<Restaurant> restaurants = restaurantRepository.findAllRestaurantOpen(nowTime, todayDay);
        if (restaurants.isEmpty()) {
            return;
        }
                restaurants.forEach((restaurant -> {
                    try {
                        InventoryRule rule = inventoryRuleRepository.findByRestaurant(restaurant)
                                .orElseThrow(()->new InventoryRuleNotFoundException("Inventory rule not found"));


                        Availability availability = availabilityRepository.findOpenNow(restaurant.getId(),
                                       todayDay,
                                        nowTime)
                                        .orElseThrow(()->new AvailabilityNotFoundException("Availability not found"));

                        Instant open = today.atTime(availability.getStart()).atZone(zone).toInstant();

                        Instant grace = ZonedDateTime.now(zone)
                                .minusMinutes(rule.getGracePeriodMin())
                                .toInstant();

                        if (grace.isBefore(open)) {
                            log.debug("Skip {}: just opened; no no-shows yet", restaurant.getName());
                            return;
                        }

                        List<Reservation> expiredReservations = reservationRepository.findByReservationBooked(
                                restaurant.getId(),
                                open,
                                grace
                                );
                        if (!expiredReservations.isEmpty()) {
                            expiredReservations.forEach(reservation -> {
                                reservation.setStatus(Status.EXPIRED);
                                reservationRepository.save(reservation);
                                notifier.notifyRestaurant(
                                        reservation.getRestaurant().getId(),
                                        reservation.getStart(),
                                        new ReservationNotifyDto("EXPIRED",
                                                reservation.getRestaurant().getId(),
                                                reservation.getStatus().name(),
                                                reservation.getStart())
                                );
                            });
                            log.info("Expired {} reservations for restaurant {}", expiredReservations.size(), restaurant.getName());

                        }


                    }catch (Exception e) {
                        log.warn("Error expiring reservations for restaurant {}: {}", restaurant.getName(), e.getMessage());
                    }
                }));
    }

    private User getUserContext(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotFoundException("User not found");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found"));

    }
}
