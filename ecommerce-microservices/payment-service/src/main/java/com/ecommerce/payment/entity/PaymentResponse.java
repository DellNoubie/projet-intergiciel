package com.ecommerce.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    // ID de l'ordre Razorpay à passer au frontend pour ouvrir le checkout
    private String razorpayOrderId;

    // Clé publique Razorpay (safe à exposer au frontend)
    private String razorpayKeyId;

    // Montant en paise
    private Integer amount;

    private String currency;

    // ID de notre transaction en base
    private Integer transactionId;

    private String status;
}
