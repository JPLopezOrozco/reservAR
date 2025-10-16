package com.reservAR.backreservar.controller;

import com.reservAR.backreservar.dto.RestaurantRequestDto;
import com.reservAR.backreservar.dto.RestaurantResponseDto;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.service.IRestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final IRestaurantService restaurantService;


    @GetMapping("/id/{id}")
    public ResponseEntity<RestaurantResponseDto> findById(@PathVariable Long id){
        Restaurant restaurant = restaurantService.findById(id);
        return ResponseEntity.ok(RestaurantResponseDto.of(restaurant));
    }

    @PostMapping
    public ResponseEntity<RestaurantResponseDto> save(@RequestBody @Valid RestaurantRequestDto restaurantRequestDto){
        Restaurant restaurant = restaurantService.create(restaurantRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurant.getId())
                .toUri();

        return ResponseEntity.created(location).body(RestaurantResponseDto.of(restaurant));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RestaurantResponseDto> update(@PathVariable Long id, @RequestBody @Valid RestaurantRequestDto restaurantRequestDto){
        Restaurant restaurant = restaurantService.update(id, restaurantRequestDto);
        return ResponseEntity.ok(RestaurantResponseDto.of(restaurant));
    }

    @PatchMapping("/price/{id}")
    public ResponseEntity<RestaurantResponseDto> updatePrice(@RequestParam BigDecimal price, @PathVariable Long id){
        Restaurant restaurant = restaurantService.changePrice(id, price);
        return ResponseEntity.ok(RestaurantResponseDto.of(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> findAll(){
        List<RestaurantResponseDto> restaurants = restaurantService.findAll().stream()
                .map(RestaurantResponseDto::of)
                .toList();
        return ResponseEntity.ok(restaurants);
    }

}
