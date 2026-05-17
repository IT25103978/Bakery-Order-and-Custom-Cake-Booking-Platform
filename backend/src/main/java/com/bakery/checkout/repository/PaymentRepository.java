package com.bakery.checkout.repository;

import com.bakery.checkout.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {


    List<Payment> findAllByOrderByTimestampDesc();

}