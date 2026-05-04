package com.ecommerce.order.client;

import com.ecommerce.order.entity.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour appeler le product-service via Eureka (lb://).
 * Si le product-service est down, le circuit breaker retourne un fallback.
 */
@FeignClient(
        name = "PRODUCT-SERVICE",
        fallback = ProductClientFallback.class
)
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Integer productId);
}
