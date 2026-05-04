package com.ecommerce.order.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Payload envoyé par le client pour passer une commande.
 * Peut contenir plusieurs produits (liste d'items).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInput {

    @NotBlank(message = "L'adresse est obligatoire")
    private String fullName;

    @NotBlank(message = "L'adresse est obligatoire")
    private String fullAddress;

    private String contactNumber;
    private String alternateContactNumber;

    // Liste des produits commandés
    @NotNull
    private List<OrderProductQuantity> orderProductQuantityList;
}
