package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.AvailabilityRequestDto;
import com.reservAR.backreservar.exception.AvailabilityException;
import com.reservAR.backreservar.exception.AvailabilityNotFoundException;
import com.reservAR.backreservar.exception.RestaurantNotFoundException;
import com.reservAR.backreservar.model.Availability;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.repository.AvailabilityRepository;
import com.reservAR.backreservar.repository.RestaurantRepository;
import com.reservAR.backreservar.service.IAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AvailabilityService implements IAvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final RestaurantRepository restaurantRepository;

    @Override

    @Transactional(readOnly = true)
    public Availability findById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(()-> new AvailabilityNotFoundException("Availability not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Availability> findByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));
        return availabilityRepository.findByRestaurant(restaurant);
    }

    @Override
    @Transactional
    public Availability save(AvailabilityRequestDto availability) {
        DayOfWeek day = DayOfWeek
                .valueOf(availability
                        .dayOfWeek()
                        .trim()
                        .toUpperCase(Locale.ROOT));

        Restaurant restaurant = restaurantRepository.findById(availability.restaurantId())
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant not found"));

        if (availability.end().isBefore(availability.start()))
            throw new AvailabilityException("End time cannot be before start time");

        if (availabilityRepository.existsOverlap(availability.restaurantId(), day, availability.start(), availability.end()))
            throw new AvailabilityException("Time range overlaps with another availability");


        Availability newAvailability = Availability.builder()
                .restaurant(restaurant)
                .dayOfWeek(day)
                .start(availability.start())
                .end(availability.end())
                .build();


        return availabilityRepository.save(newAvailability);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!availabilityRepository.existsById(id))
            throw new AvailabilityNotFoundException("Availability not found");
        availabilityRepository.deleteById(id);
    }
}
