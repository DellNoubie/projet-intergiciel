package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.entity.*;
import com.ecommerce.order.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CartService cartService;

    /**
     * Passer une commande à partir d'un OrderInput.
     *
     * @param userEmail  email de l'utilisateur connecté (extrait du header X-Auth-User)
     * @param orderInput données de la commande
     * @param fromCart   si true, vider le panier après commande
     */
    @Transactional
    public List<OrderDetail> placeOrder(String userEmail, OrderInput orderInput, boolean fromCart) {
        List<OrderDetail> savedOrders = new ArrayList<>();

        for (OrderProductQuantity opq : orderInput.getOrderProductQuantityList()) {
            // Récupérer le produit depuis product-service via Feign
            ProductDTO product = productClient.getProductById(opq.getProductId());

            if ("SERVICE INDISPONIBLE".equals(product.getProductName())) {
                throw new RuntimeException("Service produit indisponible — commande annulée.");
            }

            OrderDetail order = new OrderDetail();
            order.setUserEmail(userEmail);
            order.setProductId(product.getProductId());
            order.setProductName(product.getProductName());
            order.setProductPrice(product.getProductDiscountedPrice());
            order.setQuantity(opq.getQuantity());
            order.setOrderAddress(orderInput.getFullAddress());
            order.setOrderAmount(product.getProductDiscountedPrice() * opq.getQuantity());
            order.setOrderStatus("PLACED");

            savedOrders.add(orderDetailRepository.save(order));
        }

        // Si commande depuis le panier, on vide le panier
        if (fromCart) {
            cartService.clearCart(userEmail);
        }

        return savedOrders;
    }

    /** Historique de commandes de l'utilisateur connecté */
    public List<OrderDetail> getMyOrders(String userEmail) {
        return orderDetailRepository.findByUserEmailOrderByOrderDateDesc(userEmail);
    }

    /** Toutes les commandes — Admin seulement */
    @PreAuthorize("hasRole('Admin')")
    public List<OrderDetail> getAllOrders() {
        return orderDetailRepository.findAll();
    }

    /** Mettre à jour le statut d'une commande — Admin seulement */
    @PreAuthorize("hasRole('Admin')")
    public OrderDetail updateOrderStatus(Integer orderId, String newStatus) {
        OrderDetail order = orderDetailRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : " + orderId));
        order.setOrderStatus(newStatus);
        return orderDetailRepository.save(order);
    }

    public OrderDetail getOrderById(Integer orderId) {
        return orderDetailRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : " + orderId));
    }
}
