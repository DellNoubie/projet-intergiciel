package com.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    // Référence utilisateur (email — pas de FK directe vers auth-service)
    private String userEmail;

    // Snapshot produit au moment de la commande
    private Integer productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;

    // Adresse de livraison
    private String orderAddress;

    // Statut : PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    private String orderStatus;

    // Montant total de la commande
    private Double orderAmount;

    private LocalDateTime orderDate;

    @PrePersist
    public void prePersist() {
        this.orderDate = LocalDateTime.now();
        if (this.orderStatus == null) this.orderStatus = "PLACED";
    }
}
