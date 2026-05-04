package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Recherche par catégorie
    List<Product> findByProductCategory(String category);

    // Recherche par nom (insensible à la casse)
    List<Product> findByProductNameContainingIgnoreCase(String name);

    // Pagination
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByProductCategory(String category, Pageable pageable);
}
