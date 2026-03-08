package com.reservAR.backreservar.service;


import com.reservAR.backreservar.dto.ReservationNotifyDto;
import com.reservAR.backreservar.dto.ReservationRequestDto;
import com.reservAR.backreservar.exception.*;
import com.reservAR.backreservar.model.*;
import com.reservAR.backreservar.repository.*;
import com.reservAR.backreservar.service.impl.ReservationService;
import com.reservAR.backreservar.websocket.ReservationNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private InventoryRuleRepository inventoryRuleRepository;
    @Mock private TableEntityRepository tableEntityRepository;
    @Mock private UserRepository userRepository;
    @Mock private AvailabilityRepository availabilityRepository;
    @Mock private ReservationNotifier notifier;

    @InjectMocks
    private ReservationService reservationService;

    private Restaurant restaurant;
    private User user;
    private InventoryRule rule;
    private TableEntity table;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Don Julio")
                .price(new BigDecimal("5000"))
                .build();

        user = User.builder()
                .id(10L)
                .email("user@mail.com")
                .name("Juan")
                .surname("Lopez")
                .role(Role.CUSTOMER)
                .build();

        rule = InventoryRule.builder()
                .id(1L)
                .restaurant(restaurant)
                .defaultDurationMin(90)
                .prepBufferMin(15)
                .cleanupBufferMin(15)
                .gracePeriodMin(15)
                .build();

        table = TableEntity.builder()
                .id(100L)
                .restaurant(restaurant)
                .code("M1")
                .minCapacity(2)
                .maxCapacity(4)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@mail.com", null, List.of())
        );
    }

    @Test
    void shouldCreateReservationSuccessfully() {
        LocalDateTime startDate = LocalDateTime.of(2026, 3, 10, 20, 0);
        ReservationRequestDto dto = new ReservationRequestDto(1L, null, startDate, 2);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(inventoryRuleRepository.findByRestaurant(restaurant)).thenReturn(Optional.of(rule));
        when(availabilityRepository.isOpen(eq(restaurant), eq(DayOfWeek.TUESDAY),
                eq(LocalTime.of(20, 0)), eq(LocalTime.of(21, 30)))).thenReturn(true);
        when(tableEntityRepository.findFreeTablesByRestaurant(eq(1L), any(), any()))
                .thenReturn(List.of(table));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(999L);
            return r;
        });

        Reservation result = reservationService.save(dto);

        assertNotNull(result);
        assertEquals(Status.BOOKED, result.getStatus());
        assertEquals(1, result.getTables().size());
        assertEquals("M1", result.getTables().get(0).getCode());

        verify(notifier).notifyRestaurant(eq(1L), any(Instant.class), any(ReservationNotifyDto.class));
    }

    @Test
    void shouldThrowWhenRestaurantIsClosed() {
        LocalDateTime startDate = LocalDateTime.of(2026, 3, 10, 20, 0);
        ReservationRequestDto dto = new ReservationRequestDto(1L, null, startDate, 2);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(inventoryRuleRepository.findByRestaurant(restaurant)).thenReturn(Optional.of(rule));
        when(availabilityRepository.isOpen(any(), any(), any(), any())).thenReturn(false);

        assertThrows(RestaurantException.class, () -> reservationService.save(dto));
    }

    @Test
    void shouldThrowWhenNoFreeTableMatchesPartySize() {
        LocalDateTime startDate = LocalDateTime.of(2026, 3, 10, 20, 0);
        ReservationRequestDto dto = new ReservationRequestDto(1L, null, startDate, 6);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(inventoryRuleRepository.findByRestaurant(restaurant)).thenReturn(Optional.of(rule));
        when(availabilityRepository.isOpen(any(), any(), any(), any())).thenReturn(true);
        when(tableEntityRepository.findFreeTablesByRestaurant(eq(1L), any(), any()))
                .thenReturn(List.of(table));

        assertThrows(TableNotFoundException.class, () -> reservationService.save(dto));
    }

    @Test
    void shouldCancelBookedReservation() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .restaurant(restaurant)
                .user(user)
                .status(Status.BOOKED)
                .start(Instant.now())
                .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.cancelReservation(1L);

        assertEquals(Status.CANCELLED, result.getStatus());
        verify(notifier).notifyRestaurant(eq(1L), any(), any(ReservationNotifyDto.class));
    }

    @Test
    void shouldThrowWhenCancelReservationIsNotBooked() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(Status.COMPLETED)
                .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(ReservationStatusException.class, () -> reservationService.cancelReservation(1L));
    }

    @Test
    void shouldCompleteBookedReservation() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .restaurant(restaurant)
                .user(user)
                .status(Status.BOOKED)
                .start(Instant.now())
                .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.completeReservation(1L);

        assertEquals(Status.COMPLETED, result.getStatus());
        verify(notifier).notifyRestaurant(eq(1L), any(), any(ReservationNotifyDto.class));
    }
}