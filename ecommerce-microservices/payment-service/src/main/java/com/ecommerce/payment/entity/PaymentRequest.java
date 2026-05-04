package com.ecommerce.payment.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "L'id de commande est obligatoire")
    private Integer orderId;

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être positif")
    private Double amount;  // En INR — on convertit en paise dans le service

    private String currency = "INR";
}
