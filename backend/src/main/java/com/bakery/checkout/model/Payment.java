package com.bakery.checkout.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



 @Entity
 @Table(name = "payment")
 public class Payment {

     @Id
     private String id;

     private String cardHolderName;
     private String maskedCardNumber;
     private String expiryDate;
     private double originalAmount;
     private String promoCode;
     private double discountAmount;
     private double finalAmount;
     private String timestamp;

     public Payment() {
     }

     public Payment(String id, String cardHolderName, String maskedCardNumber,
                   String expiryDate, double originalAmount, String promoCode,
                   double discountAmount, double finalAmount, String timestamp) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.maskedCardNumber = maskedCardNumber;
        this.expiryDate = expiryDate;
        this.originalAmount = originalAmount;
        this.promoCode = promoCode;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.timestamp = timestamp;
    }

     public String getId() {
         return id;
     }

     public void setId(String id) {
         this.id = id;
     }

     public String getCardHolderName() {
         return cardHolderName;
     }

     public void setCardHolderName(String cardHolderName) {
         this.cardHolderName = cardHolderName;
     }

     public String getMaskedCardNumber() {
         return maskedCardNumber;
     }

     public void setMaskedCardNumber(String maskedCardNumber) {
         this.maskedCardNumber = maskedCardNumber;
     }

     public String getExpiryDate() {
         return expiryDate;
     }

     public void setExpiryDate(String expiryDate) {
         this.expiryDate = expiryDate;
     }

     public double getOriginalAmount() {
         return originalAmount;
     }

     public void setOriginalAmount(double originalAmount) {
         this.originalAmount = originalAmount;
     }

     public String getPromoCode() {
         return promoCode;
     }

     public void setPromoCode(String promoCode) {
         this.promoCode = promoCode;
     }

     public double getDiscountAmount() {
         return discountAmount;
     }

     public void setDiscountAmount(double discountAmount) {
         this.discountAmount = discountAmount;
     }

     public double getFinalAmount() {
         return finalAmount;
     }

     public void setFinalAmount(double finalAmount) {
         this.finalAmount = finalAmount;
     }

     public String getTimestamp() {
         return timestamp;
     }

     public void setTimestamp(String timestamp) {
         this.timestamp = timestamp;
     }
 }
