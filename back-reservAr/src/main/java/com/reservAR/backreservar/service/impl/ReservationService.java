package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.ReservationRequestDto;
import com.reservAR.backreservar.exception.*;
import com.reservAR.backreservar.model.*;
import com.reservAR.backreservar.repository.*;
import com.reservAR.backreservar.service.IReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return reservationRepository.save(newReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUser(Long id) {
        User user = userRepository.findById(id)
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
            return reservationRepository.save(reservation);
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
            return reservationRepository.save(reservation);
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
                            });
                            reservationRepository.saveAll(expiredReservations);
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
