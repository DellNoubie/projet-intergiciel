package com.ecommerce.product.controller;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
@Tag(name = "Products", description = "Gestion des produits")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ─── Public ────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Lister tous les produits")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/paged")
    @Operation(summary = "Lister les produits avec pagination")
    public ResponseEntity<Page<Product>> getProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProductsPaged(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par ID")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrer par catégorie")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher par nom")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProducts(name));
    }

    // ─── Admin uniquement ──────────────────────────────────────────

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    @Operation(
            summary = "Ajouter un produit (Admin)",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductFormRequest.class),
                            encoding = {
                                    @Encoding(name = "product",   contentType = "application/json"),
                                    @Encoding(name = "imageFile",  contentType = "image/png, image/jpeg")
                            }
                    )
            )
    )
    public ResponseEntity<?> addProduct(
            @org.springframework.web.bind.annotation.RequestPart("product") @Valid Product product,
            @org.springframework.web.bind.annotation.RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product saved = productService.addProduct(product, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du traitement de l'image : " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    @Operation(
            summary = "Modifier un produit (Admin)",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductFormRequest.class),
                            encoding = {
                                    @Encoding(name = "product",   contentType = "application/json"),
                                    @Encoding(name = "imageFile",  contentType = "image/png, image/jpeg")
                            }
                    )
            )
    )
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestPart("product") @Valid Product product,
            @org.springframework.web.bind.annotation.RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product updated = productService.updateProduct(id, product, imageFile);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur image : " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Produit supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─── Schema Swagger pour le multipart ─────────────────────────

    @Schema(name = "ProductFormRequest")
    static class ProductFormRequest {
        @Schema(
                description = "Données JSON du produit",
                example = "{\"productName\":\"iPhone 15\",\"productDescription\":\"Smartphone Apple\",\"productActualPrice\":999.99,\"productDiscountedPrice\":899.99,\"productCategory\":\"Electronics\"}"
        )
        public Product product;

        @Schema(description = "Image PNG ou JPEG (optionnel, max 10MB)", type = "string", format = "binary")
        public MultipartFile imageFile;
    }
}