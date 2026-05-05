package com.bakery.service;

import com.bakery.model.Checkout;
import com.bakery.model.Product;
import com.bakery.repository.CheckoutRepository;
import com.bakery.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final ProductRepository productRepository;

    public CheckoutService(CheckoutRepository checkoutRepository,
                           ProductRepository productRepository) {
        this.checkoutRepository = checkoutRepository;
        this.productRepository = productRepository;
    }

    // ── PLACE ORDER ───────────────────────────────
    public Checkout placeOrder(Checkout checkout) {
        // Find the product and calculate total price
        Product product = productRepository.findById(checkout.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + checkout.getProductId()));

        // Check stock availability
        if (product.getStockQuantity() < checkout.getQuantity()) {
            throw new RuntimeException("Not enough stock for: " + product.getName());
        }

        // Calculate total price
        checkout.setTotalPrice(product.getPrice() * checkout.getQuantity());
        checkout.setStatus("PENDING");

        // Reduce stock
        product.setStockQuantity(product.getStockQuantity() - checkout.getQuantity());
        if (product.getStockQuantity() == 0) {
            product.setAvailable(false);
        }
        productRepository.save(product);

        return checkoutRepository.save(checkout);
    }

    // ── GET ALL ORDERS ────────────────────────────
    public List<Checkout> getAllOrders() {
        return checkoutRepository.findAll();
    }

    // ── GET ORDER BY ID ───────────────────────────
    public Checkout getOrderById(Long id) {
        return checkoutRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id));
    }

    // ── GET ORDERS BY STATUS ──────────────────────
    public List<Checkout> getOrdersByStatus(String status) {
        return checkoutRepository.findByStatus(status);
    }

    // ── UPDATE ORDER STATUS ───────────────────────
    public Checkout updateOrderStatus(Long id, String status) {
        Checkout existing = getOrderById(id);
        existing.setStatus(status);
        return checkoutRepository.save(existing);
    }

    // ── CANCEL ORDER ──────────────────────────────
    public void cancelOrder(Long id) {
        Checkout existing = getOrderById(id);

        // Restore stock when order is cancelled
        Product product = productRepository.findById(existing.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + existing.getProductId()));

        product.setStockQuantity(product.getStockQuantity() + existing.getQuantity());
        product.setAvailable(true);
        productRepository.save(product);

        checkoutRepository.deleteById(id);
    }
}

