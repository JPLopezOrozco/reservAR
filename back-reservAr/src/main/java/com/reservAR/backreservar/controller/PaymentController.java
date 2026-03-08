package com.reservAR.backreservar.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.reservAR.backreservar.dto.PaymentDto;
import com.reservAR.backreservar.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/mercado-pago/{id}")
    public ResponseEntity<String> mercado(@PathVariable Long id) throws MPException, MPApiException {
        return ResponseEntity.ok(paymentService.paymentReservation(id));
    }


    @PostMapping("/mercado-pago/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody(required = false)Map<String, Object> body,
                                                @RequestHeader Map<String, Object> headers,
                                                @RequestParam(required = false) Map<String, String> params)
            throws MPException, MPApiException {
        paymentService.processPayment(body, headers, params);

        return ResponseEntity.ok().body("Ok");
    }
}
