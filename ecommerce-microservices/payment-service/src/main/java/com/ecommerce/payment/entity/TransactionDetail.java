package com.ecommerce.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    // Email de l'utilisateur (issu du header X-Auth-User)
    private String userEmail;

    // ID de commande liée
    private Integer orderId;

    // Identifiant Razorpay de l'ordre de paiement
    private String razorpayOrderId;

    // Montant en paise (1 INR = 100 paise) — unité Razorpay
    private Integer amountInPaise;

    // Devise : INR par défaut
    private String currency;

    // Statut : CREATED, PAID, FAILED
    private String status;

    // ID de paiement Razorpay (disponible après confirmation)
    private String razorpayPaymentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = "CREATED";
        if (this.currency == null) this.currency = "INR";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
