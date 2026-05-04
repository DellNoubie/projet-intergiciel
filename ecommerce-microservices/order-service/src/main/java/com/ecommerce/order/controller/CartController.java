package com.ecommerce.order.controller;

import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * L'email de l'utilisateur est extrait du header X-Auth-User
     * injecté par l'API Gateway — plus besoin de variable statique !
     */

    /** POST /cart/add?productId=1&quantity=2 */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestHeader("X-Auth-User") String userEmail,
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            Cart cart = cartService.addToCart(userEmail, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /cart → voir mon panier */
    @GetMapping
    public ResponseEntity<List<Cart>> getMyCart(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(cartService.getCartByUser(userEmail));
    }

    /** GET /cart/total → total du panier */
    @GetMapping("/total")
    public ResponseEntity<?> getCartTotal(
            @RequestHeader("X-Auth-User") String userEmail) {
        Double total = cartService.getCartTotal(userEmail);
        return ResponseEntity.ok(Map.of("total", total, "userEmail", userEmail));
    }

    /** DELETE /cart/{cartId} → supprimer un item du panier */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeItem(
            @PathVariable Integer cartId,
            @RequestHeader("X-Auth-User") String userEmail) {
        try {
            cartService.removeItemFromCart(cartId, userEmail);
            return ResponseEntity.ok(Map.of("message", "Item supprimé du panier"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /cart/clear → vider tout le panier */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(
            @RequestHeader("X-Auth-User") String userEmail) {
        cartService.clearCart(userEmail);
        return ResponseEntity.ok(Map.of("message", "Panier vidé"));
    }
}
