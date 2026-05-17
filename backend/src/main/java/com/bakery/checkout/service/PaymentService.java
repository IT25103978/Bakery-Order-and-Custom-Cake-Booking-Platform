package com.bakery.checkout.service;

import com.bakery.checkout.model.Payment;
import com.bakery.checkout.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    //CREAT
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    //READ ALL
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByTimestampDesc();
    }

    //READ ONE
    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }

    //Check EXISTS
    public boolean paymentExists(String id) {
        return paymentRepository.existsById(id);
    }

    public boolean deleteCreditCardDetails(String id) {
        if (!paymentRepository.existsById(id)) {
            return false;
        }

        paymentRepository.deleteById(id);
        return true;
    }

    // UPDATE
    public Payment updatePayment(String id, Payment updatedPayment) {
        Optional<Payment> existingPaymentOptional = paymentRepository.findById(id);

        if (existingPaymentOptional.isPresent()) {
            Payment existingPayment = existingPaymentOptional.get();

            existingPayment.setCardHolderName(updatedPayment.getCardHolderName());
            existingPayment.setMaskedCardNumber(updatedPayment.getMaskedCardNumber());
            existingPayment.setExpiryDate(updatedPayment.getExpiryDate());
            existingPayment.setOriginalAmount(updatedPayment.getOriginalAmount());
            existingPayment.setPromoCode(updatedPayment.getPromoCode());
            existingPayment.setDiscountAmount(updatedPayment.getDiscountAmount());
            existingPayment.setFinalAmount(updatedPayment.getFinalAmount());
            existingPayment.setTimestamp(updatedPayment.getTimestamp());

            return paymentRepository.save(existingPayment);
        }

        return null;
    }

    public long getPaymentCount() {
        return paymentRepository.count();
    }
}
