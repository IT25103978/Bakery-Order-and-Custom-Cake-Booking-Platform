package com.bakery.service;

import com.bakery.dto.CheckoutRequest;
import com.bakery.dto.PromoUpdateResponse;
import com.bakery.model.*;
import com.bakery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartService cartService;
    private final PromotionCodeRepository promoRepository;
    private final SavedCardRepository cardRepository;
    private final OrderRepository orderRepository;

    // Calculate discount based on your PromotionCode model structure
    public PromoUpdateResponse calculateDiscount(Long userId, String promoCode) {
        Cart cart = cartService.getOrCreateCart(userId);

        double originalTotal = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        if (promoCode == null || promoCode.isBlank()) {
            return new PromoUpdateResponse(originalTotal, 0.0, originalTotal, "No promo code applied.");
        }

        Optional<PromotionCode> promoOpt = promoRepository.findByPromoCodeAndActiveTrue(promoCode);

        if (promoOpt.isPresent()) {
            double percentage = promoOpt.get().getDiscountPercentage();
            double discountAmount = (originalTotal * percentage) / 100;
            double finalTotal = originalTotal - discountAmount;
            return new PromoUpdateResponse(originalTotal, discountAmount, finalTotal, "Promo applied successfully!");
        }

        return new PromoUpdateResponse(originalTotal, 0.0, originalTotal, "Invalid or expired promo code.");
    }

    // Master processing method mapping cart contents to static generated records
    @Transactional
    public Order processCheckout(CheckoutRequest request) {
        Cart cart = cartService.getOrCreateCart(request.userId());
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty shopping cart.");
        }

        // Generate totals matching current code structures
        PromoUpdateResponse totals = calculateDiscount(request.userId(), request.promoCode());

        // Transform cart items to immutable order history line item items
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> OrderItem.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getProduct().getPrice())
                .subtotal(cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .build()).collect(Collectors.toList());

        // Build main tracking invoice schema context matching your pre-built Order components
        Order order = Order.builder()
                .user(cart.getUser())
                .orderItems(orderItems)
                .totalPrice(totals.finalTotal()) // Set final reduced price to total_price
                .orderStatus("PENDING")          // Initial confirmation state
                .deliveryAddress(request.deliveryAddress())
                .phoneNumber(request.phoneNumber())
                .build();

        // Handle card vaulting logic cleanly
        if (request.rememberCard() && request.cardNumber() != null && !request.cardNumber().isBlank()) {
            saveCardDetails(cart.getUser(), request);
        }

        Order savedOrder = orderRepository.save(order);

        // Wipe active items from cart upon successful database persistence transaction
        cartService.clearCart(request.userId());

        return savedOrder;
    }

    private void saveCardDetails(User user, CheckoutRequest request) {
        String rawNumber = request.cardNumber().replaceAll("\\s+", "");
        String maskedCard = "**** **** **** " + rawNumber.substring(Math.max(0, rawNumber.length() - 4));

        SavedCard card = SavedCard.builder()
                .user(user)
                .cardholderName(request.cardholderName())
                .cardNumber(maskedCard)
                .expiryDate(request.expiryDate())
                .build();
        cardRepository.save(card);
    }

    @Transactional
    public void deleteSavedCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    public List<SavedCard> getSavedCardsForUser(Long userId) {
        return cardRepository.findByUserId(userId);
    }
}