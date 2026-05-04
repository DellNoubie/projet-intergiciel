package com.ecommerce.order.controller;

import com.ecommerce.order.entity.OrderDetail;
import com.ecommerce.order.entity.OrderInput;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * POST /orders/place?fromCart=true
     * Passer une commande. Si fromCart=true, le panier est vidé après.
     */
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(
            @RequestHeader("X-Auth-User") String userEmail,
            @Valid @RequestBody OrderInput orderInput,
            @RequestParam(defaultValue = "false") boolean fromCart) {
        try {
            List<OrderDetail> orders = orderService.placeOrder(userEmail, orderInput, fromCart);
            return ResponseEntity.status(HttpStatus.CREATED).body(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /orders/my → mes commandes */
    @GetMapping("/my")
    public ResponseEntity<List<OrderDetail>> getMyOrders(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(orderService.getMyOrders(userEmail));
    }

    /** GET /orders/{id} → une commande par id */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /orders/admin/all → toutes les commandes (Admin) */
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderDetail>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /** PUT /orders/admin/{id}/status?status=SHIPPED → changer le statut (Admin) */
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            OrderDetail updated = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
