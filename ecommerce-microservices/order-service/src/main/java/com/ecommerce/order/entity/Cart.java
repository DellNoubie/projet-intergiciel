package com.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    // Email de l'utilisateur propriétaire du panier
    // (on n'a plus de référence directe vers User — autre service)
    private String userEmail;

    // ID du produit (référence vers product-service, pas de FK directe)
    private Integer productId;

    // Nom + prix snapshottés au moment de l'ajout au panier
    // pour éviter les appels Feign à chaque lecture du panier
    private String productName;
    private Double productPrice;

    private Integer quantity;

    // ✅ Fix du bug monolithe : @ManyToOne (plusieurs items peuvent avoir le même produit)
    // On ne met plus @OneToOne ici !
}
