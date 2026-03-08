package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.TableRequestDto;
import com.reservAR.backreservar.exception.RestaurantNotFoundException;
import com.reservAR.backreservar.exception.TableException;
import com.reservAR.backreservar.exception.TableNotFoundException;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.TableEntity;
import com.reservAR.backreservar.repository.RestaurantRepository;
import com.reservAR.backreservar.repository.TableEntityRepository;
import com.reservAR.backreservar.service.impl.TableEntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableEntityServiceTest {

    @Mock
    private TableEntityRepository tableEntityRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private TableEntityService tableEntityService;

    @Test
    void shouldSaveTable() {
        Restaurant restaurant = Restaurant.builder().id(1L).name("Don Julio").build();
        TableRequestDto dto = new TableRequestDto(1L, "M1", 2, 4);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(tableEntityRepository.existsByRestaurantAndCodeIgnoreCase(restaurant, "M1")).thenReturn(false);
        when(tableEntityRepository.save(any(TableEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        TableEntity result = tableEntityService.save(dto);

        assertEquals("M1", result.getCode());
        assertEquals(2, result.getMinCapacity());
        assertEquals(4, result.getMaxCapacity());
    }

    @Test
    void shouldThrowWhenMinCapacityGreaterThanMax() {
        TableRequestDto dto = new TableRequestDto(1L, "M1", 5, 4);

        assertThrows(TableException.class, () -> tableEntityService.save(dto));
        verifyNoInteractions(restaurantRepository);
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        TableRequestDto dto = new TableRequestDto(1L, "M1", 2, 4);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> tableEntityService.save(dto));
    }

    @Test
    void shouldThrowWhenCodeAlreadyExists() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        TableRequestDto dto = new TableRequestDto(1L, "M1", 2, 4);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(tableEntityRepository.existsByRestaurantAndCodeIgnoreCase(restaurant, "M1")).thenReturn(true);

        assertThrows(TableException.class, () -> tableEntityService.save(dto));
    }

    @Test
    void shouldFindByRestaurantId() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(tableEntityRepository.findByRestaurant(restaurant)).thenReturn(List.of(
                TableEntity.builder().id(1L).code("M1").build()
        ));

        List<TableEntity> result = tableEntityService.findByRestaurantId(1L);

        assertEquals(1, result.size());
        assertEquals("M1", result.get(0).getCode());
    }

    @Test
    void shouldDeleteTable() {
        TableEntity table = TableEntity.builder().id(1L).build();
        when(tableEntityRepository.findById(1L)).thenReturn(Optional.of(table));

        tableEntityService.deleteById(1L);

        verify(tableEntityRepository).delete(table);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingTable() {
        when(tableEntityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TableNotFoundException.class, () -> tableEntityService.deleteById(1L));
    }
}