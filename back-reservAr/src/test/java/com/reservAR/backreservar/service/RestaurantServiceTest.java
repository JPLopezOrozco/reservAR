package com.reservAR.backreservar.service;


import com.reservAR.backreservar.dto.RestaurantRequestDto;
import com.reservAR.backreservar.exception.DuplicateRestaurantException;
import com.reservAR.backreservar.exception.RestaurantException;
import com.reservAR.backreservar.exception.RestaurantNotFoundException;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.repository.RestaurantRepository;
import com.reservAR.backreservar.service.impl.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    void shouldCreateRestaurantWithZeroPriceWhenPriceIsNull() {
        RestaurantRequestDto dto = new RestaurantRequestDto("Don Julio", "Calle 123", "Buenos Aires", null);

        Restaurant saved = Restaurant.builder()
                .id(1L)
                .name("Don Julio")
                .address("Calle 123")
                .city("Buenos Aires")
                .price(BigDecimal.ZERO)
                .build();

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(saved);

        Restaurant result = restaurantService.create(dto);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getPrice());
        assertEquals("Don Julio", result.getName());
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void shouldFindRestaurantById() {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Don Julio", result.getName());
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.findById(99L));
    }

    @Test
    void shouldUpdateRestaurant() {
        Restaurant existing = Restaurant.builder()
                .id(1L)
                .name("Viejo")
                .address("Vieja")
                .city("CABA")
                .price(BigDecimal.TEN)
                .build();

        RestaurantRequestDto dto = new RestaurantRequestDto("Nuevo", "Nueva", "La Plata", BigDecimal.ONE);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(restaurantRepository.existsByNameAndAddressAndCityIgnoreCaseAndIdNot("Nuevo", "Nueva", "La Plata", 1L))
                .thenReturn(false);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant result = restaurantService.update(1L, dto);

        assertEquals("Nuevo", result.getName());
        assertEquals("Nueva", result.getAddress());
        assertEquals("La Plata", result.getCity());
    }

    @Test
    void shouldThrowWhenUpdatingDuplicateRestaurant() {
        Restaurant existing = Restaurant.builder().id(1L).build();
        RestaurantRequestDto dto = new RestaurantRequestDto("Don Julio", "Calle 123", "CABA", BigDecimal.TEN);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(restaurantRepository.existsByNameAndAddressAndCityIgnoreCaseAndIdNot("Don Julio", "Calle 123", "CABA", 1L))
                .thenReturn(true);

        assertThrows(DuplicateRestaurantException.class, () -> restaurantService.update(1L, dto));
    }

    @Test
    void shouldChangePrice() {
        Restaurant restaurant = Restaurant.builder().id(1L).price(BigDecimal.ZERO).build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant result = restaurantService.changePrice(1L, new BigDecimal("2500"));

        assertEquals(new BigDecimal("2500"), result.getPrice());
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        Restaurant restaurant = Restaurant.builder().id(1L).price(BigDecimal.ZERO).build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        assertThrows(RestaurantException.class,
                () -> restaurantService.changePrice(1L, new BigDecimal("-1")));
    }

    @Test
    void shouldReturnAllRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(List.of(
                Restaurant.builder().id(1L).name("A").build(),
                Restaurant.builder().id(2L).name("B").build()
        ));

        List<Restaurant> result = restaurantService.findAll();

        assertEquals(2, result.size());
    }
}