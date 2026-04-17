package com.bakery.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // "Chocolate Cake"

    private String category;       // "cake", "cupcake", "pastry"

    private String description;    // "Rich chocolate layered cake"

    private Double price;          // 25.99

    private String imageUrl;       // "images/choco-cake.jpg"

    private Integer stockQuantity; // How many available

    private Boolean available;     // true or false

    private String status;  // AVAILABLE, OUT_OF_STOCK
}

