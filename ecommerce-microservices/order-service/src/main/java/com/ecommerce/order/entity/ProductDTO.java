package com.ecommerce.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un produit tel que retourné par le product-service.
 * On ne stocke QUE ce dont on a besoin pour la commande.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Integer productId;
    private String productName;
    private Double productDiscountedPrice;
    private Double productActualPrice;
    private String productCategory;
}
