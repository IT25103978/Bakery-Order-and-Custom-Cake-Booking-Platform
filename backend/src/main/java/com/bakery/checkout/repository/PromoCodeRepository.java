package com.bakery.checkout.repository;

import com.bakery.checkout.model.PromoCode;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class PromoCodeRepository {

    private static final String FILE_PATH = "data/promoCodes.txt";


    public PromoCodeRepository() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        if (!file.exists() || file.length() == 0) {
            seedSampleData();
        }
    }


    private void seedSampleData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            writer.write("BAKERY10|10.0|true");  writer.newLine();
            writer.write("SWEET20|20.0|true");   writer.newLine();
            writer.write("CAKE15|15.0|true");    writer.newLine();
            writer.write("EXPIRED5|5.0|false");  writer.newLine();
        } catch (IOException e) {
            System.err.println("[PromoCodeRepository] Error seeding promo codes: " + e.getMessage());
        }
    }

    // ─── READ:
    public PromoCode findByCode(String code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    PromoCode promo = PromoCode.fromFileString(line);
                    // Case-insensitive comparison
                    if (promo.getCode().equalsIgnoreCase(code)) {
                        return promo;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[PromoCodeRepository] Error reading promo codes: " + e.getMessage());
        }
        return null;
    }

    // ─── READ: Get all promo codes (for admin viewing) ─────────
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
