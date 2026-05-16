// ===================================================
// CartService.java  — com.bakery.service
// All business logic for cart operations.
// ===================================================
package com.bakery.service;

import com.bakery.model.*;
import com.bakery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor          // Lombok injects all final fields via constructor
@Transactional
public class CartService {

    private final CartRepository        cartRepository;
    private final CartItemRepository    cartItemRepository;
    private final ProductRepository     productRepository;
    private final UserRepository        userRepository;

    // ─────────────────────────────────────────────────
    //  GET or CREATE a cart for the given user
    // ─────────────────────────────────────────────────
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    // ─────────────────────────────────────────────────
    //  ADD a product to the cart
    //  • If the product is already in the cart → increment qty
    //  • Otherwise               → create a new CartItem
    // ─────────────────────────────────────────────────
    public Cart addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(quantity)
                                    .build();
                            cart.getCartItems().add(newItem);
                            cartItemRepository.save(newItem);
                        }
                );

        return cartRepository.save(cart);
    }

    // ─────────────────────────────────────────────────
    //  UPDATE quantity for a specific CartItem
    // ─────────────────────────────────────────────────
    public void updateQuantity(Long cartItemId, int newQuantity) {
        if (newQuantity <= 0) {
            removeCartItem(cartItemId);
            return;
        }
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found: " + cartItemId));
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
    }

    // ─────────────────────────────────────────────────
    //  REMOVE a single line from the cart
    // ─────────────────────────────────────────────────
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // ─────────────────────────────────────────────────
    //  CLEAR the entire cart (all items)
    // ─────────────────────────────────────────────────
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    // ─────────────────────────────────────────────────
    //  READ — fetch the full cart with items
    // ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Cart getCart(Long userId) {
        return getOrCreateCart(userId);
    }

    // ─────────────────────────────────────────────────
    //  READ — list all CartItems for a user's cart
    // ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return getOrCreateCart(userId).getCartItems();
    }
}
