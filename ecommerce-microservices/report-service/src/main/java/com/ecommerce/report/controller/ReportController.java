package com.ecommerce.report.controller;

import com.ecommerce.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;


    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrdersReport() {
        try {
            byte[] pdf = reportService.generateAllOrdersReport();
            return buildPdfResponse(pdf, "rapport-commandes.pdf");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur génération rapport : " + e.getMessage()));
        }
    }

    /**
     * GET /reports/invoice/{orderId}
     * Facture d'une commande — accessible par l'utilisateur concerné ou Admin.
     */
    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<?> getInvoice(@PathVariable Integer orderId) {
        try {
            byte[] pdf = reportService.generateInvoice(orderId);
            return buildPdfResponse(pdf, "facture-" + orderId + ".pdf");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur génération facture : " + e.getMessage()));
        }
    }

    /**
     * GET /reports/products
     * Catalogue produits — Admin uniquement.
     */
    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/products")
    public ResponseEntity<?> getProductsReport() {
        try {
            byte[] pdf = reportService.generateProductsReport();
            return buildPdfResponse(pdf, "catalogue-produits.pdf");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur génération catalogue : " + e.getMessage()));
        }
    }

    // ─── Helper ────────────────────────────────────────────────────

    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdf, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(pdf.length);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
