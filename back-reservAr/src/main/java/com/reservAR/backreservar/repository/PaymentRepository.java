package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentIntent, Long> {
    Optional<PaymentIntent> findByExternalReference(String externalReference);
}
