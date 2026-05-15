// ===================================================
// CartController.java  — com.bakery.controller
// Handles all HTTP routes under /cart
// ===================================================
package com.bakery.controller;

import com.bakery.model.Cart;
import com.bakery.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // ─────────────────────────────────────────────────
    //  VIEW CART  —  GET /cart/{userId}
    // ─────────────────────────────────────────────────
    @GetMapping("/{userId}")
    public String viewCart(@PathVariable Long userId, Model model) {
        Cart cart = cartService.getCart(userId);
        model.addAttribute("cart",      cart);
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("total",     cart.getTotalPrice());
        model.addAttribute("userId",    userId);
        return "cart";               // → templates/cart.html
    }

    // ─────────────────────────────────────────────────
    //  ADD TO CART  —  POST /cart/add
    // ─────────────────────────────────────────────────
    @PostMapping("/add")
    public String addToCart(@RequestParam Long   userId,
                            @RequestParam Long   productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes  ra) {
        try {
            cartService.addToCart(userId, productId, quantity);
            ra.addFlashAttribute("successMsg", "Item added to cart!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Could not add item: " + e.getMessage());
        }
        return "redirect:/cart/" + userId;
    }

    // ─────────────────────────────────────────────────
    //  UPDATE QUANTITY  —  POST /cart/update
    // ─────────────────────────────────────────────────
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId,
                                 @RequestParam int  quantity,
                                 @RequestParam Long userId,
                                 RedirectAttributes ra) {
        cartService.updateQuantity(cartItemId, quantity);
        ra.addFlashAttribute("successMsg", "Cart updated.");
        return "redirect:/cart/" + userId;
    }

    // ─────────────────────────────────────────────────
    //  REMOVE ITEM  —  POST /cart/remove/{cartItemId}
    // ─────────────────────────────────────────────────
    @PostMapping("/remove/{cartItemId}")
    public String removeItem(@PathVariable Long cartItemId,
                             @RequestParam Long userId,
                             RedirectAttributes ra) {
        cartService.removeCartItem(cartItemId);
        ra.addFlashAttribute("successMsg", "Item removed.");
        return "redirect:/cart/" + userId;
    }

    // ─────────────────────────────────────────────────
    //  CLEAR CART  —  POST /cart/clear
    // ─────────────────────────────────────────────────
    @PostMapping("/clear")
    public String clearCart(@RequestParam Long userId, RedirectAttributes ra) {
        cartService.clearCart(userId);
        ra.addFlashAttribute("successMsg", "Cart cleared.");
        return "redirect:/cart/" + userId;
    }

    // ─────────────────────────────────────────────────
    //  PROCEED TO CHECKOUT  —  GET /cart/{userId}/checkout
    // ─────────────────────────────────────────────────
    @GetMapping("/{userId}/checkout")
    public String proceedToCheckout(@PathVariable Long userId, Model model) {
        Cart cart = cartService.getCart(userId);
        if (cart.getCartItems().isEmpty()) {
            model.addAttribute("errorMsg", "Your cart is empty.");
            return "redirect:/cart/" + userId;
        }
        model.addAttribute("cart",   cart);
        model.addAttribute("total",  cart.getTotalPrice());
        model.addAttribute("userId", userId);
        return "redirect:/checkout?userId=" + userId;   // → your existing CheckoutController
    }
}
