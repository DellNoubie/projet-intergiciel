package com.ecommerce.product.config;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            initProducts();
            System.out.println("✅ DataInitializer — " + productRepository.count() + " produits créés avec succès !");
        } else {
            System.out.println("✅ DataInitializer — Produits déjà existants (" + productRepository.count() + "), skip.");
        }
    }

    private void initProducts() {
        List<Product> products = List.of(

                // ─── Electronics ──────────────────────────────────────
                createProduct(
                        "iPhone 15 Pro",
                        "Smartphone Apple avec puce A17 Pro, appareil photo 48MP et Dynamic Island.",
                        1299.99, 1199.99, "Electronics"
                ),
                createProduct(
                        "Samsung Galaxy S24 Ultra",
                        "Smartphone Samsung avec S-Pen intégré, écran 6.8 pouces et 200MP.",
                        1199.99, 1099.99, "Electronics"
                ),
                createProduct(
                        "MacBook Pro M3",
                        "Laptop Apple avec puce M3, 16GB RAM, 512GB SSD et écran Liquid Retina XDR.",
                        2499.99, 2299.99, "Electronics"
                ),
                createProduct(
                        "Sony WH-1000XM5",
                        "Casque Bluetooth avec réduction de bruit active leader du marché.",
                        399.99, 319.99, "Electronics"
                ),
                createProduct(
                        "iPad Pro 12.9\"",
                        "Tablette Apple avec puce M2, écran Liquid Retina XDR et compatibilité Apple Pencil.",
                        1099.99, 999.99, "Electronics"
                ),

                // ─── Clothing ─────────────────────────────────────────
                createProduct(
                        "Nike Air Max 270",
                        "Chaussures de running avec amorti Air Max visible et semelle légère.",
                        149.99, 119.99, "Clothing"
                ),
                createProduct(
                        "Levi's 501 Original Jeans",
                        "Jean iconique coupe droite en denim 100% coton, disponible en bleu stonewash.",
                        89.99, 69.99, "Clothing"
                ),
                createProduct(
                        "The North Face Puffer Jacket",
                        "Doudoune chaude et légère, idéale pour l'hiver, résistante à l'eau.",
                        249.99, 199.99, "Clothing"
                ),

                // ─── Home & Kitchen ───────────────────────────────────
                createProduct(
                        "Nespresso Vertuo Next",
                        "Machine à café capsules avec technologie Centrifusion et connexion Bluetooth.",
                        179.99, 149.99, "Home & Kitchen"
                ),
                createProduct(
                        "Dyson V15 Detect",
                        "Aspirateur balai sans fil avec laser pour détecter la poussière invisible.",
                        699.99, 599.99, "Home & Kitchen"
                ),
                createProduct(
                        "KitchenAid Stand Mixer",
                        "Robot pâtissier 4.8L avec 10 vitesses et accessoires inclus, coloris rouge.",
                        449.99, 379.99, "Home & Kitchen"
                ),

                // ─── Books ────────────────────────────────────────────
                createProduct(
                        "Clean Code - Robert C. Martin",
                        "Le livre de référence sur l'écriture de code propre et maintenable.",
                        49.99, 39.99, "Books"
                ),
                createProduct(
                        "Designing Data-Intensive Applications",
                        "Guide complet sur l'architecture des systèmes distribués modernes.",
                        54.99, 44.99, "Books"
                ),
                createProduct(
                        "Spring Boot in Action",
                        "Maîtrisez Spring Boot pour créer des applications Java robustes.",
                        44.99, 34.99, "Books"
                ),

                // ─── Sports ───────────────────────────────────────────
                createProduct(
                        "Tapis de Yoga Premium",
                        "Tapis antidérapant 6mm d'épaisseur, écologique et lavable en machine.",
                        59.99, 44.99, "Sports"
                ),
                createProduct(
                        "Vélo de Route Trek Domane",
                        "Vélo de route en carbone, idéal pour le cyclisme sur longue distance.",
                        2999.99, 2699.99, "Sports"
                )
        );

        productRepository.saveAll(products);
    }

    private Product createProduct(String name, String description,
                                  double actualPrice, double discountedPrice,
                                  String category) {
        Product p = new Product();
        p.setProductName(name);
        p.setProductDescription(description);
        p.setProductActualPrice(actualPrice);
        p.setProductDiscountedPrice(discountedPrice);
        p.setProductCategory(category);
        p.setProductImage(null); // Pas d'image au démarrage — ajoutables via API
        return p;
    }
}
