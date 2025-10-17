package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.TableRequestDto;
import com.reservAR.backreservar.exception.RestaurantNotFoundException;
import com.reservAR.backreservar.exception.TableException;
import com.reservAR.backreservar.exception.TableNotFoundException;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.TableEntity;
import com.reservAR.backreservar.repository.RestaurantRepository;
import com.reservAR.backreservar.repository.TableEntityRepository;
import com.reservAR.backreservar.service.ITableEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableEntityService implements ITableEntityService {

    private final TableEntityRepository tableEntityRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional(readOnly = true)
    public TableEntity findById(Long id) {
        return tableEntityRepository.findById(id)
                .orElseThrow(()-> new TableNotFoundException("Table not found"));
    }


    @Override
    @Transactional
    public TableEntity save(TableRequestDto tableEntity) {
        if (tableEntity.minCapacity() > tableEntity.maxCapacity())
            throw new TableException("Minimum cannot be greater than maximum");
        Restaurant restaurant = restaurantRepository.findById(tableEntity.restaurantId())
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));
        if (tableEntityRepository.existsByRestaurantAndCodeIgnoreCase(restaurant, tableEntity.code()))
            throw new TableException("This code already exists in the restaurant");

        TableEntity newTable = TableEntity.builder()
                .restaurant(restaurant)
                .code(tableEntity.code())
                .minCapacity(tableEntity.minCapacity())
                .maxCapacity(tableEntity.maxCapacity())
                .build();

        return tableEntityRepository.save(newTable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableEntity> findByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));
        return tableEntityRepository.findByRestaurant(restaurant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        TableEntity tableEntity = tableEntityRepository.findById(id)
                .orElseThrow(()-> new TableNotFoundException("Table not found"));
        tableEntityRepository.delete(tableEntity);
    }
}
