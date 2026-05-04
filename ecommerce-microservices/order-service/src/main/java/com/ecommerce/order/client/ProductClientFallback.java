package com.ecommerce.order.client;

import com.ecommerce.order.entity.ProductDTO;
import org.springframework.stereotype.Component;

/**
 * Fallback retourné si le product-service ne répond pas.
 * Évite une cascade d'erreurs (circuit breaker pattern).
 */
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductDTO getProductById(Integer productId) {
        // On retourne un produit vide avec un flag d'erreur
        ProductDTO fallback = new ProductDTO();
        fallback.setProductId(productId);
        fallback.setProductName("SERVICE INDISPONIBLE");
        fallback.setProductDiscountedPrice(0.0);
        return fallback;
    }
}
