package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByNameAndAddressAndCityIgnoreCase(String name, String address, String city);
    boolean existsByNameAndAddressAndCityIgnoreCaseAndIdNot(String name, String address, String city, Long id);


}
