package com.reservAR.backreservar.service;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.reservAR.backreservar.dto.PaymentDto;

import java.util.Map;

public interface IPaymentService {
    String paymentReservation(Long id) throws MPException, MPApiException;
    void processPayment(Map<String, Object> body, Map<String, Object> headers, Map<String, String> queryParams) throws MPException, MPApiException;
}
