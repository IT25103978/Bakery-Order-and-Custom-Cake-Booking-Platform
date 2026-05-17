package com.bakery.checkout.controller;

import com.bakery.checkout.model.Payment;
import com.bakery.checkout.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PaymentController — REST API endpoints for managing Payment records directly.
 *
 * Base URL: /api/payments
 *
 * This is separate from CheckoutController:
 *   - CheckoutController  = full checkout flow (promo code + payment processing)
 *   - PaymentController   = direct CRUD on payment records (admin/management use)
 *
 * Endpoints:
 *   GET    /api/payments              → READ   — get all payment records
 *   GET    /api/payments/{id}         → READ   — get one payment by ID
 *   POST   /api/payments              → CREATE — save a new payment record
 *   DELETE /api/payments/{id}         → DELETE — delete a payment record
 *
 * @RestController  = handles HTTP requests and returns JSON responses
 * @CrossOrigin     = allows the HTML frontend to call these endpoints
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    // Spring automatically injects PaymentService here
    @Autowired
    private PaymentService paymentService;

    // ─── READ: GET /api/payments ──────────────────────────────────────────────────

    /**
     * Returns all payment records from the database.
     * The frontend uses this to populate the payment history table.
     *
     * Example: GET http://localhost:8080/api/payments
     *
     * @return List of all Payment objects as JSON.
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments); // 200 OK + JSON array
    }

    // ─── READ: GET /api/payments/{id} ────────────────────────────────────────────

    /**
     * Returns a single payment record by its ID.
     *
     * Example: GET http://localhost:8080/api/payments/PAY001
     *
     * @param id The payment ID from the URL path (e.g. "PAY001").
     * @return The Payment object as JSON, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable String id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);

        if (payment.isPresent()) {
            return ResponseEntity.ok(payment.get()); // 200 OK + payment JSON
        } else {
            // Return 404 with an error message
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Payment with ID " + id + " was not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ─── CREATE: POST /api/payments ───────────────────────────────────────────────

    /**
     * Saves a new payment record directly to the database.
     * Note: For full checkout processing (with promo codes), use CheckoutController.
     * This endpoint is for direct payment record creation (e.g. admin use).
     *
     * Example request body:
     * {
     *   "id": "PAY005",
     *   "cardHolderName": "Jane Doe",
     *   "cardNumber": "**** **** **** 4242",
     *   "expiryDate": "06/27",
     *   "originalAmount": 3500.00,
     *   "promoCodeUsed": "",
     *   "discountAmount": 0.0,
     *   "finalAmount": 3500.00,
     *   "timestamp": "2024-12-01 10:00:00"
     * }
     *
     * @param payment Payment object from the request body (auto-parsed from JSON).
     * @return The saved Payment object, or 500 if saving failed.
     */
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        try {
            // Validate that required fields are present
            if (payment.getId() == null || payment.getId().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Payment ID is required.");
                return ResponseEntity.badRequest().body(error);
            }

            if (payment.getCardHolderName() == null || payment.getCardHolderName().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Card holder name is required.");
                return ResponseEntity.badRequest().body(error);
            }

            // Save the payment to MySQL
            Payment saved = paymentService.savePayment(payment);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to save payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ─── UPDATE: PUT /api/payments/{id} ─────────────────────────────────────────

    /**
     * Updates an existing payment record by its ID.
     *
     * Example: PUT http://localhost:8080/api/payments/PAY001
     *
     * @param id The payment ID from the URL path.
     * @param updatedPayment The new payment details from request body.
     * @return Updated Payment object, or 404 if ID not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(
            @PathVariable String id,
            @RequestBody Payment updatedPayment) {

        Payment payment = paymentService.updatePayment(id, updatedPayment);

        if (payment == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Payment with ID " + id + " was not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(payment);
    }

    // ─── DELETE: DELETE /api/payments/{id} ───────────────────────────────────────

    /**
     * Deletes a payment record from the database by its ID.
     * The frontend Delete button calls this endpoint.
     *
     * Example: DELETE http://localhost:8080/api/payments/PAY001
     *
     * @param id The payment ID from the URL path.
     * @return 200 OK if deleted, 404 if ID not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePayment(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();

        boolean deleted = paymentService.deleteCreditCardDetails(id);

        if (deleted) {
            result.put("success", true);
            result.put("message", "Payment " + id + " has been deleted successfully.");
            return ResponseEntity.ok(result); // 200 OK
        } else {
            result.put("success", false);
            result.put("message", "Payment with ID " + id + " was not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result); // 404
        }
    }
}
