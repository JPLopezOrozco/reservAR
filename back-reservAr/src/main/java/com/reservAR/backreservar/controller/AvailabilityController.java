package com.reservAR.backreservar.controller;

import com.reservAR.backreservar.dto.AvailabilityRequestDto;
import com.reservAR.backreservar.dto.AvailabilityResponseDto;
import com.reservAR.backreservar.model.Availability;
import com.reservAR.backreservar.service.IAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final IAvailabilityService availabilityService;


    @GetMapping("/id/{id}")
    public ResponseEntity<AvailabilityResponseDto> findById(@PathVariable Long id){
        Availability availability = availabilityService.findById(id);
        return ResponseEntity.ok(AvailabilityResponseDto.of(availability));
    }

    @PostMapping
    public ResponseEntity<AvailabilityResponseDto> create(@RequestBody AvailabilityRequestDto availabilityRequestDto){
        Availability availability = availabilityService.save(availabilityRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(availability.getId())
                .toUri();
        return ResponseEntity.created(location).body(AvailabilityResponseDto.of(availability));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        availabilityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<AvailabilityResponseDto>> findByRestaurantId(@PathVariable Long id){
        List<AvailabilityResponseDto> availabilities = availabilityService.findByRestaurantId(id).stream()
                .map(AvailabilityResponseDto::of)
                .toList();
        return ResponseEntity.ok(availabilities);
    }

}
