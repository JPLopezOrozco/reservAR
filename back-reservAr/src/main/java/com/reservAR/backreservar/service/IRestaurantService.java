package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.RestaurantRequestDto;
import com.reservAR.backreservar.model.Restaurant;

import java.math.BigDecimal;
import java.util.List;

public interface IRestaurantService {
    Restaurant findById(Long id);
    Restaurant create(RestaurantRequestDto res);
    Restaurant update(Long id,RestaurantRequestDto res);
    Restaurant changePrice(Long id, BigDecimal newPrice);
    List<Restaurant> findAll();
}
