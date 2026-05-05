package com.bakery.repository;

import com.bakery.model.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    List<Checkout> findByStatus(String status);
    List<Checkout> findByProductId(Long productId);
    List<Checkout> findByFullNameContainingIgnoreCase(String name);
}