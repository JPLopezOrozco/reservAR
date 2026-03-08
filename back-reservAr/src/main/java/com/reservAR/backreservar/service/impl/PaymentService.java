package com.reservAR.backreservar.service.impl;


import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.reservAR.backreservar.dto.ReservationWsEvent;
import com.reservAR.backreservar.exception.PaymentNotException;
import com.reservAR.backreservar.exception.ReservationNotFoundException;
import com.reservAR.backreservar.model.PaymentIntent;
import com.reservAR.backreservar.model.PaymentStatus;
import com.reservAR.backreservar.model.Reservation;
import com.reservAR.backreservar.model.Status;
import com.reservAR.backreservar.repository.PaymentRepository;
import com.reservAR.backreservar.repository.ReservationRepository;
import com.reservAR.backreservar.service.IPaymentService;
import com.reservAR.backreservar.websocket.ReservationNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationNotifier notifier;



    @Value("${mercado-pago.webhook-url}")
    private String WEBHOOK_URL;


    @Override
    @Transactional
    public String paymentReservation(Long id) throws MPException, MPApiException {


        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ReservationNotFoundException("Reservation not found with id: " + id));

        PaymentIntent paymentIntent = PaymentIntent.builder()
                .reservation(reservation)
                .provider("MERCADO_PAGO")
                .amount(reservation.getRestaurant().getPrice())
                .currency("ARS")
                .status(PaymentStatus.PENDING)
                .build();

        String externalReference = "reservation-" + reservation.getId() + UUID.randomUUID();

        try {


            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id("reservation-" + reservation.getId())
                    .title("Reserva #" + reservation.getId())
                    .description("Pago de reserva")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(paymentIntent.getAmount())
                    .build();

            log.info("success={}", "http://localhost:3000/payments/success");
            log.info("failure={}", "http://localhost:3000/payments/failure");
            log.info("pending={}", "http://localhost:3000/payments/pending");

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:3000/payments/success")
                    .failure("http://localhost:3000/payments/failure")
                    .pending("http://localhost:3000/payments/pending")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .externalReference(externalReference)
                    .notificationUrl(WEBHOOK_URL)
                    .backUrls(backUrls)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            paymentIntent.setPreferenceId(preference.getId());
            paymentIntent.setInitPoint(preference.getInitPoint());
            paymentIntent.setExternalReference(externalReference);
            paymentRepository.save(paymentIntent);

            return preference.getSandboxInitPoint();
        } catch (MPApiException e) {
            log.error("MP status code{}", e.getStatusCode());
            log.error("MP content{}", e.getApiResponse().getContent());
            throw e;
        }
    }

    @Override
    @Transactional
    public void processPayment(Map<String, Object> body,
                               Map<String, Object> headers,
                               Map<String, String> queryParams) throws MPException, MPApiException {

        String type = queryParams.get("type");
        if (type == null) {
            type = queryParams.get("topic");
        }

        if (!"payment".equals(type)) {
            return;
        }

        String paymentId = queryParams.get("data.id");

        if (paymentId == null && body.get("data") instanceof Map<?,?> data) {
            Object id = data.get("id");
            if (id != null) {
                paymentId = String.valueOf(id);
            }
        }

        if (paymentId == null) {
            return;
        }

        PaymentClient client = new PaymentClient();
        Payment payment = client.get(Long.valueOf(paymentId));

        String externalReference = payment.getExternalReference();

        PaymentIntent paymentIntent = paymentRepository.findByExternalReference(externalReference)
                .orElseThrow(()-> new PaymentNotException("Payment not found with external reference: " + externalReference));

        paymentIntent.setPaymentProviderId(String.valueOf(payment.getId()));

        if ("approved".equals(payment.getStatus())){
            if (paymentIntent.getStatus() != PaymentStatus.SUCCEEDED) {
                paymentIntent.setStatus(PaymentStatus.SUCCEEDED);

                Reservation reservation = paymentIntent.getReservation();
                reservation.setStatus(Status.BOOKED);
                reservationRepository.save(reservation);

                ReservationWsEvent event = new ReservationWsEvent(
                        reservation.getId(),
                        reservation.getStatus().name(),
                        reservation.getRestaurant().getId(),
                        reservation.getStart()
                );

                notifier.notifyRestaurant(reservation.getRestaurant().getId(), reservation.getStart(), event);
            }
        }else if ("rejected".equals(payment.getStatus())) {
            paymentIntent.setStatus(PaymentStatus.FAILED);
        } else if ("cancelled".equals(payment.getStatus())) {
            paymentIntent.setStatus(PaymentStatus.CANCELLED);
        }else {
            paymentIntent.setStatus(PaymentStatus.PENDING);
        }
        paymentRepository.save(paymentIntent);

    }
}
