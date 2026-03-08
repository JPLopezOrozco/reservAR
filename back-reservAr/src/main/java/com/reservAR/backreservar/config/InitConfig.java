package com.reservAR.backreservar.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {

    @Value("${mercado-pago.access-token}")
    private String ACCESS_TOKEN;

    @PostConstruct
    public void initMercadoPago() {
        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
    }
}
