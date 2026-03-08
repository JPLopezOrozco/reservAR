package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.AvailabilityRequestDto;
import com.reservAR.backreservar.exception.AvailabilityException;
import com.reservAR.backreservar.exception.AvailabilityNotFoundException;
import com.reservAR.backreservar.exception.RestaurantNotFoundException;
import com.reservAR.backreservar.model.Availability;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.repository.AvailabilityRepository;
import com.reservAR.backreservar.repository.RestaurantRepository;
import com.reservAR.backreservar.service.impl.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void shouldSaveAvailability() {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        AvailabilityRequestDto dto = new AvailabilityRequestDto(1L, "MONDAY",
                LocalTime.of(12, 0), LocalTime.of(15, 0));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(availabilityRepository.existsOverlap(1L, DayOfWeek.MONDAY,
                LocalTime.of(12, 0), LocalTime.of(15, 0))).thenReturn(false);
        when(availabilityRepository.save(any(Availability.class))).thenAnswer(inv -> inv.getArgument(0));

        Availability result = availabilityService.save(dto);

        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        assertEquals(LocalTime.of(12, 0), result.getStart());
        assertEquals(LocalTime.of(15, 0), result.getEnd());
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        AvailabilityRequestDto dto = new AvailabilityRequestDto(1L, "MONDAY",
                LocalTime.of(12, 0), LocalTime.of(15, 0));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> availabilityService.save(dto));
    }

    @Test
    void shouldThrowWhenEndBeforeStart() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        AvailabilityRequestDto dto = new AvailabilityRequestDto(1L, "MONDAY",
                LocalTime.of(15, 0), LocalTime.of(12, 0));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        assertThrows(AvailabilityException.class, () -> availabilityService.save(dto));
    }

    @Test
    void shouldThrowWhenOverlapExists() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        AvailabilityRequestDto dto = new AvailabilityRequestDto(1L, "MONDAY",
                LocalTime.of(12, 0), LocalTime.of(15, 0));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(availabilityRepository.existsOverlap(1L, DayOfWeek.MONDAY,
                LocalTime.of(12, 0), LocalTime.of(15, 0))).thenReturn(true);

        assertThrows(AvailabilityException.class, () -> availabilityService.save(dto));
    }

    @Test
    void shouldFindAvailabilityByRestaurantId() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(availabilityRepository.findByRestaurant(restaurant)).thenReturn(List.of(
                Availability.builder().id(1L).restaurant(restaurant).dayOfWeek(DayOfWeek.MONDAY).build()
        ));

        List<Availability> result = availabilityService.findByRestaurantId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldDeleteAvailability() {
        when(availabilityRepository.existsById(1L)).thenReturn(true);

        availabilityService.deleteById(1L);

        verify(availabilityRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingAvailability() {
        when(availabilityRepository.existsById(1L)).thenReturn(false);

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.deleteById(1L));
    }
}