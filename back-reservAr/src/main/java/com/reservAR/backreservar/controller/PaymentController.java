package com.reservAR.backreservar.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {


    @GetMapping("/mercado")
    public String mercado() throws MPException, MPApiException {

        MercadoPagoConfig.setAccessToken("APP_USR-5631774668529393-102316-efa56a3acc039021ad8a03ca7f229df7-2943677250");

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                        .success("https://www.tu-sitio/success")
                        .pending("https://www.tu-sitio/pending")
                        .failure("https://www.tu-sitio/failure")
                        .build();

        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id("1234")
                        .title("Games")
                        .description("PS5")
                        .pictureUrl("http://picture.com/PS5")
                        .categoryId("games")
                        .quantity(2)
                        .currencyId("ARS")
                        .unitPrice(new BigDecimal("4000"))
                        .build();

        List<PreferenceItemRequest> items = new ArrayList<>();

        items.add(itemRequest);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .build();

        PreferenceClient client = new PreferenceClient();

        Preference preference = client.create(preferenceRequest);

        return preference.getSandboxInitPoint();
    }
}
