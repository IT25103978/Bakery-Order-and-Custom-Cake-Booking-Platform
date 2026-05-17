package com.bakery.checkout.service;

import com.bakery.checkout.model.Checkout;
import com.bakery.checkout.repository.CheckoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;

    public CheckoutService(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    // CREATE - place order using dummy product data
    public Checkout placeOrder(Checkout checkout) {


        if (checkout.getTotalPrice() == null) {
            checkout.setTotalPrice(3000.0 * checkout.getQuantity());
        }

        // Set order status
        checkout.setStatus("PENDING");

        // Save checkout to database
        return checkoutRepository.save(checkout);
    }

    // READ
    public List<Checkout> getAllOrders() {
        return checkoutRepository.findAll();
    }

    // READ -
    public Checkout getOrderById(Long id) {
        return checkoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    // READ
    public List<Checkout> getOrdersByStatus(String status) {
        return checkoutRepository.findByStatus(status);
    }

    // UPDATE
    public Checkout updateOrderStatus(Long id, String status) {
        Checkout existing = getOrderById(id);
        existing.setStatus(status);
        return checkoutRepository.save(existing);
    }

    // UPDATE full order details
    public Checkout updateOrder(Long id, Checkout updatedCheckout) {
        Checkout existing = getOrderById(id);

        existing.setFullName(updatedCheckout.getFullName());
        existing.setEmail(updatedCheckout.getEmail());
        existing.setPhoneNumber(updatedCheckout.getPhoneNumber());
        existing.setPreferredDeliveryDate(updatedCheckout.getPreferredDeliveryDate());
        existing.setDeliveryAddress(updatedCheckout.getDeliveryAddress());
        existing.setProductId(updatedCheckout.getProductId());
        existing.setQuantity(updatedCheckout.getQuantity());
        existing.setTotalPrice(updatedCheckout.getTotalPrice());
        existing.setStatus(updatedCheckout.getStatus());

        return checkoutRepository.save(existing);
    }

    // DELETE
    public void cancelOrder(Long id) {
        checkoutRepository.deleteById(id);
    }
}