package com.bakery.checkout.repository;

import com.bakery.checkout.model.PromoCode;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PromoCodeRepository handles file read/write operations for promo codes.
 *
 * Data is stored in: data/promoCodes.txt
 * Each line = one promo code in pipe-separated format.
 *
 * CRUD role: READ — look up a promo code by its string to get the discount.
 *
 * Pre-populate promoCodes.txt with sample data:
 *   BAKERY10|10.0|true
 *   SWEET20|20.0|true
 *   CAKE15|15.0|true
 *   EXPIRED5|5.0|false
 */
@Repository
public class PromoCodeRepository {

    private static final String FILE_PATH = "data/promoCodes.txt";

    // ─── Constructor: seed sample promo codes if file is empty ──────────────────

    public PromoCodeRepository() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        if (!file.exists() || file.length() == 0) {
            // Seed the file with sample promo codes for demonstration
            seedSampleData();
        }
    }

    /**
     * Writes default promo codes to promoCodes.txt when the file is first created.
     */
    private void seedSampleData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            writer.write("BAKERY10|10.0|true");  writer.newLine(); // 10% off
            writer.write("SWEET20|20.0|true");   writer.newLine(); // 20% off
            writer.write("CAKE15|15.0|true");    writer.newLine(); // 15% off
            writer.write("EXPIRED5|5.0|false");  writer.newLine(); // Inactive
        } catch (IOException e) {
            System.err.println("[PromoCodeRepository] Error seeding promo codes: " + e.getMessage());
        }
    }

    // ─── READ: Find a promo code by its code string ─────────────────────────────

    /**
     * Searches for a promo code in promoCodes.txt by its code string.
     * This implements the READ operation — user enters a code and we look it up.
     *
     * @param code The promo code entered by the user (case-insensitive).
     * @return The matching PromoCode if found and active, null otherwise.
     */
    public PromoCode findByCode(String code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    PromoCode promo = PromoCode.fromFileString(line);
                    // Case-insensitive comparison
                    if (promo.getCode().equalsIgnoreCase(code)) {
                        return promo; // Found it — return even if inactive (caller checks)
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[PromoCodeRepository] Error reading promo codes: " + e.getMessage());
        }
        return null; // Code not found
    }

    // ─── READ: Get all promo codes (for admin viewing) ──────────────────────────

    /**
     * Returns all promo codes stored in the file.
     */
    public List<PromoCode> getAllPromoCodes() {
        List<PromoCode> codes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    codes.add(PromoCode.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("[PromoCodeRepository] Error reading promo codes: " + e.getMessage());
        }
        return codes;
    }
}
