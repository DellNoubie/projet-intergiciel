package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.ProductDTO;
import com.ecommerce.order.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductClient productClient;

    /**
     * Ajouter un produit au panier.
     * Si le produit est déjà dans le panier, on incrémente la quantité.
     */
    public Cart addToCart(String userEmail, Integer productId, Integer quantity) {
        // Récupérer les infos produit depuis product-service (via Feign)
        ProductDTO product = productClient.getProductById(productId);

        if ("SERVICE INDISPONIBLE".equals(product.getProductName())) {
            throw new RuntimeException("Le service produit est indisponible, réessayez plus tard.");
        }

        // Vérifier si le produit est déjà dans le panier
        Optional<Cart> existingCart = cartRepository.findByUserEmailAndProductId(userEmail, productId);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }

        // Nouveau produit dans le panier — snapshot du prix au moment de l'ajout
        Cart cart = new Cart();
        cart.setUserEmail(userEmail);
        cart.setProductId(productId);
        cart.setProductName(product.getProductName());
        cart.setProductPrice(product.getProductDiscountedPrice());
        cart.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    public List<Cart> getCartByUser(String userEmail) {
        return cartRepository.findByUserEmail(userEmail);
    }

    public void removeItemFromCart(Integer cartId, String userEmail) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Item introuvable dans le panier"));

        // Vérifier que l'item appartient bien à l'utilisateur
        if (!cart.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Accès interdit : cet item ne vous appartient pas");
        }

        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }

    public Double getCartTotal(String userEmail) {
        List<Cart> items = cartRepository.findByUserEmail(userEmail);
        return items.stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
    }
}
