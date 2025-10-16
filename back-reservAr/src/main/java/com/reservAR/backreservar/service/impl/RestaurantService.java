package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.RestaurantRequestDto;
import com.reservAR.backreservar.exception.RestaurantException;
import com.reservAR.backreservar.exception.DuplicateRestaurantException;
import com.reservAR.backreservar.exception.RestaurantNotFoundException;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.repository.RestaurantRepository;
import com.reservAR.backreservar.service.IRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService implements IRestaurantService {

    private final RestaurantRepository resRepository;

    @Override
    @Transactional(readOnly = true)
    public Restaurant findById(Long id) {
        return resRepository.findById(id)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));
    }

    @Override
    @Transactional
    public Restaurant create(RestaurantRequestDto res) {
        Restaurant restaurant = Restaurant.builder()
                .name(res.name())
                .address(res.address())
                .city(res.city())
                .price(res.price() != null ? res.price() : BigDecimal.ZERO)
                .build();
        return resRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Restaurant update(Long id,RestaurantRequestDto res) {
        Restaurant restaurant = resRepository.findById(id)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));

        if (resRepository.existsByNameAndAddressAndCityIgnoreCaseAndIdNot(res.name(), res.address(), res.city(), id))
            throw new DuplicateRestaurantException("Restaurant already exists");

        restaurant.setName(res.name());
        restaurant.setAddress(res.address());
        restaurant.setCity(res.city());


        return resRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Restaurant changePrice(Long id, BigDecimal price) {
        Restaurant restaurant = resRepository.findById(id)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));
        if (price.compareTo(BigDecimal.ZERO)<0) throw new RestaurantException("Price cannot be negative");
        restaurant.setPrice(price);
        return resRepository.save(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> findAll() {
        return resRepository.findAll();
    }
}
