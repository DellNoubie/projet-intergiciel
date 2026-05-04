package com.ecommerce.payment.client;

import org.springframework.stereotype.Component;

@Component
public class OrderClientFallback implements OrderClient {

    @Override
    public Object updateOrderStatus(Integer orderId, String status) {
        // Si order-service est indisponible, on log et on continue
        // Le statut sera mis à jour manuellement ou par un batch de réconciliation
        System.err.println("[FALLBACK] Impossible de mettre à jour le statut de la commande "
                + orderId + " → " + status + " : order-service indisponible.");
        return null;
    }
}
