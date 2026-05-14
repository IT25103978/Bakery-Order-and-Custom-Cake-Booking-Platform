package com.bakery.checkout.service;

import com.bakery.checkout.model.Payment;
import com.bakery.checkout.model.PromoCode;
import com.bakery.checkout.repository.PaymentRepository;
import com.bakery.checkout.repository.PromoCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CheckoutService contains all business logic for the Checkout module.
 *
 * Responsibilities:
 *  - Validate promo codes and calculate discounts
 *  - Process and save new payments (CREATE)
 *  - Retrieve payment history (READ)
 *  - Delete saved payment methods (DELETE)
 *
 * This layer sits between the Controller (HTTP) and Repository (file storage).
 */
@Service
public class CheckoutService {

    // ─── Dependencies (Injected by Spring) ──────────────────────────────────────

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    // ─── PROMO CODE: Validate and return discount info ──────────────────────────

    /**
     * Validates a promo code entered by the user and returns the discount details.
     * This is the READ operation — we read the promo code from the file.
     *
     * @param code          The promo code string entered by the user.
     * @param originalPrice The cart total before discount.
     * @return A Map with keys: valid, discountPercent, discountAmount, finalPrice, message.
     */
    public Map<String, Object> applyPromoCode(String code, double originalPrice) {
        Map<String, Object> result = new HashMap<>();

        // Look up the code in the file
        PromoCode promo = promoCodeRepository.findByCode(code);

        if (promo == null) {
            // Code doesn't exist in our records
            result.put("valid", false);
            result.put("message", "Invalid promo code. Please try again.");
            result.put("discountPercent", 0.0);
            result.put("discountAmount", 0.0);
            result.put("finalPrice", originalPrice);
            return result;
        }

        if (!promo.isActive()) {
            // Code exists but has expired or been deactivated
            result.put("valid", false);
            result.put("message", "This promo code has expired.");
            result.put("discountPercent", 0.0);
            result.put("discountAmount", 0.0);
            result.put("finalPrice", originalPrice);
            return result;
        }

        // Valid and active — calculate the discount
        double discountPercent = promo.getDiscountPercent();
        double discountAmount = Math.round((originalPrice * discountPercent / 100.0) * 100.0) / 100.0;
        double finalPrice = Math.round((originalPrice - discountAmount) * 100.0) / 100.0;

        result.put("valid", true);
        result.put("message", "Promo code applied! You saved LKR " + discountAmount);
        result.put("discountPercent", discountPercent);
        result.put("discountAmount", discountAmount);
        result.put("finalPrice", finalPrice);

        return result;
    }

    // ─── CREATE: Process a new payment and save it ──────────────────────────────

    /**
     * Processes a checkout payment by:
     * 1. Masking the card number (security — store only last 4 digits).
     * 2. Applying the promo code discount if provided.
     * 3. Generating a unique payment ID.
     * 4. Saving the payment record to payments.txt.
     *
     * @param cardHolderName  Name on the card.
     * @param rawCardNumber   Full card number (will be masked).
     * @param expiryDate      Card expiry in MM/YY format.
     * @param originalAmount  Total price of the order.
     * @param promoCode       Promo code entered (empty string if none).
     * @return A Map with success status and payment details.
     */
    public Map<String, Object> processPayment(String cardHolderName, String rawCardNumber,
                                               String expiryDate, double originalAmount,
                                               String promoCode) {
        Map<String, Object> result = new HashMap<>();

        // Step 1: Mask the card number — store only the last 4 digits for security
        String maskedCard = maskCardNumber(rawCardNumber);

        // Step 2: Apply promo code if one was provided
        double discountAmount = 0.0;
        double finalAmount = originalAmount;

        if (promoCode != null && !promoCode.trim().isEmpty()) {
            Map<String, Object> promoResult = applyPromoCode(promoCode.trim(), originalAmount);
            if ((boolean) promoResult.get("valid")) {
                discountAmount = (double) promoResult.get("discountAmount");
                finalAmount = (double) promoResult.get("finalPrice");
            }
            // If invalid promo, we still proceed with full price (frontend validates first)
        }

        // Step 3: Generate a unique ID for this payment
        String paymentId = paymentRepository.generateNextId();

        // Step 4: Get current timestamp for the record
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Step 5: Build the Payment object
        Payment payment = new Payment(
                paymentId,
                cardHolderName,
                maskedCard,       // Never store full card number
                expiryDate,
                originalAmount,
                promoCode != null ? promoCode.trim() : "",
                discountAmount,
                finalAmount,
                timestamp
        );

        // Step 6: Save to the file
        boolean saved = paymentRepository.savePayment(payment);

        if (saved) {
            result.put("success", true);
            result.put("paymentId", paymentId);
            result.put("maskedCard", maskedCard);
            result.put("originalAmount", originalAmount);
            result.put("discountAmount", discountAmount);
            result.put("finalAmount", finalAmount);
            result.put("timestamp", timestamp);
            result.put("message", "Payment successful! Your order has been placed.");
        } else {
            result.put("success", false);
            result.put("message", "Payment failed. Please try again.");
        }

        return result;
    }

    // ─── READ: Get all payment records ──────────────────────────────────────────

    /**
     * Returns all saved payment records from payments.txt.
     * Used for admin view or payment history.
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }

    // ─── DELETE: Remove a saved payment method ──────────────────────────────────

    /**
     * Deletes a payment record by its ID.
     * This implements the DELETE operation.
     *
     * @param paymentId The ID of the payment to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deletePayment(String paymentId) {
        return paymentRepository.deletePayment(paymentId);
    }

    // ─── UTILITY: Mask a card number ────────────────────────────────────────────

    /**
     * Masks all but the last 4 digits of a card number.
     * Example: "4111111111111111" → "**** **** **** 1111"
     *
     * @param cardNumber The raw card number string.
     * @return The masked version for safe storage.
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        // Remove any spaces the user may have typed
        String digits = cardNumber.replaceAll("\\s", "");
        String lastFour = digits.substring(digits.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
