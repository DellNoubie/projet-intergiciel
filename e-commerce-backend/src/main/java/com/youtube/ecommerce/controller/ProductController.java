package com.youtube.ecommerce.controller;

import com.youtube.ecommerce.entity.ImageModel;
import com.youtube.ecommerce.entity.Product;
import com.youtube.ecommerce.entity.Report;
import com.youtube.ecommerce.entity.StringResult;
import com.youtube.ecommerce.service.ProductService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    // @PreAuthorize("hasRole('Admin')")
    // @PostMapping(value = {"/addNewProduct"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // public Product addNewProduct(@RequestPart("product") Product product,
    //                              @RequestPart("imageFile") MultipartFile[] file) {
    //     try {
    //         Set<ImageModel> images = uploadImage(file);
    //         product.setProductImages(images);
    //         return productService.addNewProduct(product);
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //         return null;
    //     }

    // }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping(value = "/addNewProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addNewProduct(@RequestPart("product") Product product,
            @RequestPart("imageFile") MultipartFile[] file) {
        try {
            System.out.println("Produit reçu: " + product);
            System.out.println("Nombre de fichiers: " + file.length);
            Set<ImageModel> images = uploadImage(file);
            product.setProductImages(images);
            Product savedProduct = productService.addNewProduct(product);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Pour voir la stacktrace complète
            return new ResponseEntity<>("Erreur lors de l'ajout du produit: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // public Set<ImageModel> uploadImage(MultipartFile[] multipartFiles) throws IOException {
    //     Set<ImageModel> imageModels = new HashSet<>();

    //     for (MultipartFile file: multipartFiles) {
    //         ImageModel imageModel = new ImageModel(
    //                 file.getOriginalFilename(),
    //                 file.getContentType(),
    //                 file.getBytes()
    //         );
    //         imageModels.add(imageModel);
    //     }

    //     return imageModels;
    // }

    public Set<ImageModel> uploadImage(MultipartFile[] files) throws IOException {
    Set<ImageModel> images = new HashSet<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IOException("Fichier vide: " + file.getOriginalFilename());
            }
            if (file.getOriginalFilename() == null || file.getContentType() == null) {
                throw new IOException("Nom ou type de fichier manquant pour: " + file.getOriginalFilename());
            }
            ImageModel image = new ImageModel(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
            );
            images.add(image);
        }
        return images;
    }

    @GetMapping({"/getAllProducts"})
    public List<Product> getAllProducts(@RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "") String searchKey) {
        List<Product> result = productService.getAllProducts(pageNumber, searchKey);
        System.out.println("Result size is "+ result.size());
        return result;
    }

    @GetMapping({"/getProductDetailsById/{productId}"})
    public Product getProductDetailsById(@PathVariable("productId") Integer productId) {
        return productService.getProductDetailsById(productId);
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping({"/deleteProductDetails/{productId}"})
    public void deleteProductDetails(@PathVariable("productId") Integer productId) {
        productService.deleteProductDetails(productId);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping({"/getProductDetails/{isSingleProductCheckout}/{productId}"})
    public List<Product> getProductDetails(@PathVariable(name = "isSingleProductCheckout" ) boolean isSingleProductCheckout,
                                           @PathVariable(name = "productId")  Integer productId) {
        return productService.getProductDetails(isSingleProductCheckout, productId);
    }

    @GetMapping("/pdf")
    @ResponseBody
    public byte[] getClientsPdf(HttpServletResponse response) throws JRException, IOException {
        // Obtention de la liste des clients en base de données
        List<Product> clients = productService.getAll();
        System.out.println("Liste de clients" + clients);
        byte[] bytes = null;

        // Génération du rapport Jaspersoft
        Report report = new Report();
        try {
            bytes = JasperExportManager.exportReportToPdf(report.generateReport(clients ));

            // Envoi du rapport au client
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);

        }catch (JRException e){
            e.printStackTrace();
        }
        return bytes;
    }
}
