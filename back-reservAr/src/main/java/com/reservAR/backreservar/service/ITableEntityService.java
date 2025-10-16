package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.TableRequestDto;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.TableEntity;

import java.util.List;

public interface ITableEntityService {
    TableEntity findById(Long id);
    TableEntity save(TableRequestDto tableEntity);
    List<TableEntity> findByRestaurantId(Long restaurantId);
    void deleteById(Long id);
}
