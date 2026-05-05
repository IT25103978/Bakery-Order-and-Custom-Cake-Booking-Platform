package com.bakery.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "checkout")
@Data
@NoArgsConstructor
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String preferredDeliveryDate;
    private String deliveryAddress;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private String status;

}





