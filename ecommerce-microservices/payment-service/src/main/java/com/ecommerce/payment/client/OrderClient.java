package com.ecommerce.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Client Feign pour mettre à jour le statut d'une commande dans order-service
 * après confirmation du paiement.
 */
@FeignClient(name = "ORDER-SERVICE", fallback = OrderClientFallback.class)
public interface OrderClient {

    @PutMapping("/orders/admin/{id}/status")
    Object updateOrderStatus(
            @PathVariable("id") Integer orderId,
            @RequestParam("status") String status
    );
}
