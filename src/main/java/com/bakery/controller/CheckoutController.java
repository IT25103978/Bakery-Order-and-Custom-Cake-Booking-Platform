package com.bakery.controller;

import com.bakery.dto.CheckoutRequest;
import com.bakery.dto.PromoUpdateResponse;
import com.bakery.model.Order;
import com.bakery.model.SavedCard;
import com.bakery.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342", allowCredentials = "true") // Matches project cross-origin policies
public class CheckoutController {

    private final CheckoutService checkoutService;

    // Checks valid price modifications using promoCode lookup
    @GetMapping("/apply-promo")
    public ResponseEntity<PromoUpdateResponse> applyPromo(
            @RequestParam Long userId,
            @RequestParam String promoCode) {
        return ResponseEntity.ok(checkoutService.calculateDiscount(userId, promoCode));
    }

    // Handles the placement of orders matching target schemas
    @PostMapping("/submit")
    public ResponseEntity<Order> completeCheckout(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(checkoutService.processCheckout(request));
    }

    // Fetch vaulted cards matching active profile ID
    @GetMapping("/cards/{userId}")
    public ResponseEntity<List<SavedCard>> getCards(@PathVariable Long userId) {
        return ResponseEntity.ok(checkoutService.getSavedCardsForUser(userId));
    }

    // Delete a specific saved card entity from user profile management
    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<String> removeCard(@PathVariable Long cardId) {
        checkoutService.deleteSavedCard(cardId);
        return ResponseEntity.ok("Card details removed successfully.");
    }
}