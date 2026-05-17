package com.bakery.checkout.model;

public class PromoCode {

    private final String code;
    private final double discountPercent;
    private final boolean active;

    public PromoCode(String code, double discountPercent, boolean active) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public boolean isActive() {
        return active;
    }

    public String toFileString() {
        return String.join("|",
                sanitize(code),
                String.valueOf(discountPercent),
                String.valueOf(active)
        );
    }

    public static PromoCode fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        String code = getPart(parts, 0);
        double discountPercent = parseDouble(getPart(parts, 1));
        boolean active = Boolean.parseBoolean(getPart(parts, 2));
        return new PromoCode(code, discountPercent, active);
    }

    private static String getPart(String[] parts, int index) {
        return index < parts.length ? parts[index] : "";
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/");
    }
}
