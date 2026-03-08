package com.reservAR.backreservar.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ReservationNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyRestaurant(Long restaurantId, Instant date, Object payload) {
        LocalDate serviceDate = LocalDateTime
                .ofInstant(date, ZoneId.systemDefault())
                .toLocalDate();

        String topic = String.format("/topic/restaurant/%d/date/%s", restaurantId, serviceDate);
        messagingTemplate.convertAndSend(topic, payload);
    }

}
