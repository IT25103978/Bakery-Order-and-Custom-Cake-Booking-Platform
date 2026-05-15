package com.bakery.controller;

import com.bakery.entity.CustomCakeBooking;
import com.bakery.service.CustomCakeBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cakes")
@CrossOrigin("*")
public class CustomCakeBookingController {

    @Autowired
    private CustomCakeBookingService service;

    // Create booking
    @PostMapping("/book")
    public CustomCakeBooking bookCake(@RequestBody CustomCakeBooking booking) {
        return service.createBooking(booking);
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