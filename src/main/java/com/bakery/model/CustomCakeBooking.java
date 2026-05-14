package com.bakery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_cake_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomCakeBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;


    private String cakeFlavor;

    private String cakeSize;

    private String messageOnCake;

    private String decorationType;

    private double price;

    private String status;
}