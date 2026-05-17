package com.bakery.checkout.controller;

import com.bakery.checkout.model.Checkout;
import com.bakery.checkout.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutService service;

        public CheckoutController(CheckoutService service) {
                this.service = service;
        }

    // POST place a new order
    // URL: POST http://localhost:8080/api/checkout
    @PostMapping
    public ResponseEntity<Checkout> placeOrder(
            @RequestBody Checkout checkout) {
        return ResponseEntity.ok(
                service.placeOrder(checkout));
    }

    // GET all orders
    // URL: GET http://localhost:8080/api/checkout
    @GetMapping
    public ResponseEntity<List<Checkout>> getAllOrders() {
        return ResponseEntity.ok(
                service.getAllOrders());
    }

    // GET single order
    // URL: GET http://localhost:8080/api/checkout/1
    @GetMapping("/{id}")
    public ResponseEntity<Checkout> getOrder(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                service.getOrderById(id));
    }

    // GET orders by status
    // URL: GET http://localhost:8080/api/checkout/status/PENDING
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Checkout>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(
                service.getOrdersByStatus(status));
    }

    // PUT update order status
    // URL: PUT http://localhost:8080/api/checkout/1/status?status=CONFIRMED
    @PutMapping("/{id}/status")
    public ResponseEntity<Checkout> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(
                service.updateOrderStatus(id, status));
    }

    // PUT update full order details
    // URL: PUT http://localhost:8080/api/checkout/1
    @PutMapping("/{id}")
    public ResponseEntity<Checkout> updateOrder(
            @PathVariable Long id,
            @RequestBody Checkout updatedCheckout) {

        return ResponseEntity.ok(
                service.updateOrder(id, updatedCheckout));
    }

    // DELETE cancel order
    // URL: DELETE http://localhost:8080/api/checkout/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long id) {
        service.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled!");
    }
}
