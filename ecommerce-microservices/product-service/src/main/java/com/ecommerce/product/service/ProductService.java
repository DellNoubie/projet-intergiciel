package com.ecommerce.product.service;

import com.ecommerce.product.entity.ImageModel;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ─── Lecture ───────────────────────────────────────────────────

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productName").ascending());
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'id : " + id));
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByProductCategory(category);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }

    // ─── Écriture (Admin seulement) ────────────────────────────────

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            ImageModel image = new ImageModel();
            image.setName(imageFile.getOriginalFilename());
            image.setType(imageFile.getContentType());
            image.setPicByte(imageFile.getBytes());
            product.setProductImage(image);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, Product updatedProduct, MultipartFile imageFile)
            throws IOException {
        Product existing = getProductById(id);
        existing.setProductName(updatedProduct.getProductName());
        existing.setProductDescription(updatedProduct.getProductDescription());
        existing.setProductDiscountedPrice(updatedProduct.getProductDiscountedPrice());
        existing.setProductActualPrice(updatedProduct.getProductActualPrice());
        existing.setProductCategory(updatedProduct.getProductCategory());

        if (imageFile != null && !imageFile.isEmpty()) {
            ImageModel image = new ImageModel();
            image.setName(imageFile.getOriginalFilename());
            image.setType(imageFile.getContentType());
            image.setPicByte(imageFile.getBytes());
            existing.setProductImage(image);
        }
        return productRepository.save(existing);
    }

    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit introuvable avec l'id : " + id);
        }
        productRepository.deleteById(id);
    }
}
