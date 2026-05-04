package com.ecommerce.payment.controller;

import com.ecommerce.payment.entity.*;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * POST /payment/create
     * Crée un ordre Razorpay et retourne les infos pour le frontend.
     *
     * Corps : { "orderId": 1, "amount": 499.0, "currency": "INR" }
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(
            @RequestHeader("X-Auth-User") String userEmail,
            @Valid @RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createTransaction(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /payment/verify
     * Vérifie la signature Razorpay après que le frontend a confirmé le paiement.
     *
     * Corps : { "razorpayOrderId": "...", "razorpayPaymentId": "...", "razorpaySignature": "..." }
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerificationRequest verificationRequest) {
        try {
            boolean isValid = paymentService.verifyPayment(verificationRequest);
            if (isValid) {
                return ResponseEntity.ok(Map.of(
                        "status", "PAID",
                        "message", "Paiement vérifié et confirmé avec succès"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", "FAILED",
                                "message", "Signature invalide — paiement rejeté"
                        ));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /payment/my → mes transactions
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyTransactions(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(paymentService.getMyTransactions(userEmail));
    }

    /**
     * GET /payment/order/{orderId} → transactions d'une commande
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getTransactionsByOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(paymentService.getTransactionsByOrder(orderId));
    }

    /**
     * GET /payment/admin/all → toutes les transactions (Admin)
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(paymentService.getAllTransactions());
    }
}
