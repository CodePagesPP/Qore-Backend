package com.example.Qore.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {
    public MercadoPagoConfig(@Value("${mp.access-token}") String token) {
        com.mercadopago.MercadoPagoConfig.setAccessToken(token);
    }
}
