package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findByUserEmail(String userEmail);

    Optional<Cart> findByUserEmailAndProductId(String userEmail, Integer productId);

    void deleteByUserEmail(String userEmail);
}
