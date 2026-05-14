package com.bakery.checkout.repository;

import com.bakery.checkout.model.Payment;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentRepository handles all file read/write operations for Payment records.
 *
 * Data is stored in: data/payments.txt
 * Each line = one payment record in pipe-separated format.
 *
 * CRUD operations covered:
 *   CREATE — savePayment()
 *   READ   — getAllPayments(), findById()
 *   DELETE — deletePayment()
 */
@Repository
public class PaymentRepository {

    // Path to the flat-file database (payments.txt)
    private static final String FILE_PATH = "data/payments.txt";

    // ─── Constructor: ensure file and directory exist on startup ────────────────

    public PaymentRepository() {
        File file = new File(FILE_PATH);
        // Create the 'data' directory if it doesn't exist
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile(); // Create empty payments.txt
            } catch (IOException e) {
                System.err.println("[PaymentRepository] Could not create payments.txt: " + e.getMessage());
            }
        }
    }

    // ─── CREATE: Save a new payment to the file ─────────────────────────────────

    /**
     * Appends a new Payment record to payments.txt.
     *
     * @param payment The payment object to save.
     * @return true if saved successfully, false otherwise.
     */
    public boolean savePayment(Payment payment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Write the payment as a single pipe-separated line
            writer.write(payment.toFileString());
            writer.newLine(); // Move to next line for the next record
            return true;
        } catch (IOException e) {
            System.err.println("[PaymentRepository] Error saving payment: " + e.getMessage());
            return false;
        }
    }

    // ─── READ: Load all payments from the file ──────────────────────────────────

    /**
     * Reads all payment records from payments.txt.
     *
     * @return List of Payment objects (empty list if file is empty or missing).
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Convert each file line back into a Payment object
                    payments.add(Payment.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("[PaymentRepository] Error reading payments: " + e.getMessage());
        }

        return payments;
    }

    // ─── READ: Find a specific payment by ID ────────────────────────────────────

    /**
     * Searches for a payment by its unique ID.
     *
     * @param id The payment ID to look for.
     * @return The matching Payment, or null if not found.
     */
    public Payment findById(String id) {
        List<Payment> all = getAllPayments();
        for (Payment p : all) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null; // Not found
    }

    // ─── DELETE: Remove a payment record by ID ──────────────────────────────────

    /**
     * Deletes a payment record by rewriting the file without the target record.
     *
     * @param id The payment ID to delete.
     * @return true if deleted, false if not found.
     */
    public boolean deletePayment(String id) {
        List<Payment> all = getAllPayments();
        boolean found = false;

        // Filter out the payment with matching ID
        List<Payment> remaining = new ArrayList<>();
        for (Payment p : all) {
            if (p.getId().equalsIgnoreCase(id)) {
                found = true; // Mark that we found the record to delete
            } else {
                remaining.add(p); // Keep all others
            }
        }

        if (!found) {
            return false; // Payment ID didn't exist
        }

        // Rewrite the file with only the remaining payments
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment p : remaining) {
                writer.write(p.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("[PaymentRepository] Error deleting payment: " + e.getMessage());
            return false;
        }
    }

    // ─── UTILITY: Generate a unique sequential payment ID ───────────────────────

    /**
     * Generates the next payment ID based on how many records exist.
     * Example: PAY001, PAY002, PAY003...
     *
     * @return A new unique payment ID string.
     */
    public String generateNextId() {
        int count = getAllPayments().size() + 1;
        return String.format("PAY%03d", count); // e.g., PAY001
    }
}
