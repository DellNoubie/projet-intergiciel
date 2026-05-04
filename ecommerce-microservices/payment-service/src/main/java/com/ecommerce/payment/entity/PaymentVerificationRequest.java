package com.ecommerce.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationRequest {

    // Ces 3 champs sont envoyés par le frontend après que Razorpay confirme le paiement
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
