package com.bakery.controller;

import com.bakery.model.CustomCakeBooking;
import com.bakery.model.User;
import com.bakery.repository.UserRepository;
import com.bakery.service.CustomCakeBookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cakes")
@CrossOrigin("*")
public class CustomCakeBookingController {

    @Autowired
    private CustomCakeBookingService service;

    // Create booking
    @Autowired
    private UserRepository userRepository; // Inject this repository to fetch the active User entity

    @PostMapping("/book")
    public ResponseEntity<?> bookCake(@RequestBody CustomCakeBooking booking, HttpSession session) {
        // Enforce active session check
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in to book a custom cake.");
        }

        // Set the user relationship context securely from the server session data
        User currentUser = userRepository.findById(userId).orElseThrow();
        booking.setUser(currentUser);
        booking.setCustomerName(currentUser.getFirstName() + " " + currentUser.getLastName());

        CustomCakeBooking savedBooking = service.createBooking(booking);
        return ResponseEntity.ok(savedBooking);
    }

    // Get all bookings
    @GetMapping("/all")
    public List<CustomCakeBooking> getAllBookings() {
        return service.getAllBookings();
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public CustomCakeBooking getBookingById(@PathVariable Long id) {
        return service.getBookingById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // Update booking status
    @PutMapping("/status/{id}")
    public CustomCakeBooking updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return service.updateStatus(id, status);
    }

    // Delete booking
    @DeleteMapping("/{id}")
    public String deleteBooking(@PathVariable Long id) {

        service.deleteBooking(id);

        return "Booking deleted successfully";
    }
}