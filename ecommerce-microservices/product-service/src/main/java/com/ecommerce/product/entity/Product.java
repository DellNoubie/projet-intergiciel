package com.ecommerce.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String productName;

    private String productDescription;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double productDiscountedPrice;

    @NotNull(message = "Le prix original est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double productActualPrice;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private ImageModel productImage;

    // Catégorie du produit (ex: Electronics, Clothing...)
    private String productCategory;
}
