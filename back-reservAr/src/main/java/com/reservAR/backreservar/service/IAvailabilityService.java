package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.AvailabilityRequestDto;
import com.reservAR.backreservar.model.Availability;

import java.util.List;

public interface IAvailabilityService {
    Availability findById(Long id);
    Availability save(AvailabilityRequestDto availability);
    void deleteById(Long id);
    List<Availability> findByRestaurantId(Long restaurantId);
}
