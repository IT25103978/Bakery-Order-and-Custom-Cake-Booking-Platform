package com.bakery.dto;

public record CheckoutRequest(
        Long userId,
        String promoCode,
        String deliveryAddress,
        String phoneNumber,
        boolean rememberCard,
        String cardholderName,
        String cardNumber,
        String expiryDate
) {}