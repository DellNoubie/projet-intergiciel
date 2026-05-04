package com.ecommerce.payment.service;

import com.ecommerce.payment.client.OrderClient;
import com.ecommerce.payment.entity.*;
import com.ecommerce.payment.repository.TransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

@Service
public class PaymentService {

    // ✅ Fix bug monolithe : clés lues depuis application.yml, jamais hardcodées !
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderClient orderClient;

    // ─── Création d'un ordre Razorpay ─────────────────────────────

    /**
     * Crée un ordre de paiement Razorpay et persiste la transaction en base.
     * Le frontend utilise le razorpayOrderId pour ouvrir le checkout Razorpay.
     */
    public PaymentResponse createTransaction(String userEmail, PaymentRequest request) {
        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            // Razorpay travaille en paise (1 INR = 100 paise)
            int amountInPaise = (int) (request.getAmount() * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", "order_rcpt_" + request.getOrderId());

            Order razorpayOrder = client.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            // Persister la transaction en base
            TransactionDetail transaction = new TransactionDetail();
            transaction.setUserEmail(userEmail);
            transaction.setOrderId(request.getOrderId());
            transaction.setRazorpayOrderId(razorpayOrderId);
            transaction.setAmountInPaise(amountInPaise);
            transaction.setCurrency(request.getCurrency());
            transaction.setStatus("CREATED");

            TransactionDetail saved = transactionRepository.save(transaction);

            return new PaymentResponse(
                    razorpayOrderId,
                    razorpayKeyId,   // clé publique — safe pour le frontend
                    amountInPaise,
                    request.getCurrency(),
                    saved.getTransactionId(),
                    "CREATED"
            );

        } catch (RazorpayException e) {
            throw new RuntimeException("Erreur Razorpay lors de la création de l'ordre : " + e.getMessage());
        }
    }

    // ─── Vérification de la signature Razorpay ────────────────────

    /**
     * Vérifie la signature HMAC-SHA256 envoyée par Razorpay après paiement.
     * C'est la seule façon sécurisée de confirmer qu'un paiement est légitime.
     *
     * Formule : HMAC_SHA256(razorpayOrderId + "|" + razorpayPaymentId, keySecret)
     */
    public boolean verifyPayment(PaymentVerificationRequest verificationRequest) {
        try {
            String data = verificationRequest.getRazorpayOrderId()
                    + "|"
                    + verificationRequest.getRazorpayPaymentId();

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convertir en hex et comparer avec la signature reçue
            String generatedSignature = HexFormat.of().formatHex(hash);
            boolean isValid = generatedSignature.equals(verificationRequest.getRazorpaySignature());

            if (isValid) {
                transactionRepository.findByRazorpayOrderId(verificationRequest.getRazorpayOrderId())
                        .ifPresent(transaction -> {
                            transaction.setStatus("PAID");
                            transaction.setRazorpayPaymentId(verificationRequest.getRazorpayPaymentId());
                            transactionRepository.save(transaction);
                            try {
                                orderClient.updateOrderStatus(transaction.getOrderId(), "PAID");
                            } catch (Exception ex) {
                                System.err.println("[WARN] Impossible de mettre à jour le statut de commande "
                                        + transaction.getOrderId() + " : " + ex.getMessage());
                            }
                        });
            } else {
                transactionRepository.findByRazorpayOrderId(verificationRequest.getRazorpayOrderId())
                        .ifPresent(transaction -> {
                            transaction.setStatus("FAILED");
                            transactionRepository.save(transaction);
                        });
            }

            return isValid;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la vérification de la signature : " + e.getMessage());
        }
    }

    // ─── Consultation ─────────────────────────────────────────────

    public List<TransactionDetail> getMyTransactions(String userEmail) {
        return transactionRepository.findByUserEmail(userEmail);
    }

    public List<TransactionDetail> getTransactionsByOrder(Integer orderId) {
        return transactionRepository.findByOrderId(orderId);
    }

    public List<TransactionDetail> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
