package com.ecommerce.report.service;

import com.ecommerce.report.client.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private ProductClient productClient;

    // ─── Rapport : toutes les commandes ──────────────────────────

    /**
     * ✅ Fix bug monolithe : endpoint /pdf maintenant protégé — Admin seulement.
     * Génère un PDF de toutes les commandes.
     */
    public byte[] generateAllOrdersReport() throws JRException {
        List<OrderDTO> orders = orderClient.getAllOrders();
        return generateOrderPdf(orders, "Rapport Global des Commandes");
    }

    // ─── Rapport : facture d'une commande ─────────────────────────

    public byte[] generateInvoice(Integer orderId) throws JRException {
        OrderDTO order = orderClient.getOrderById(orderId);
        List<OrderDTO> data = (order != null) ? List.of(order) : List.of();
        return generateOrderPdf(data, "Facture Commande #" + orderId);
    }

    // ─── Rapport : catalogue produits ─────────────────────────────

    public byte[] generateProductsReport() throws JRException {
        List<ProductDTO> products = productClient.getAllProducts();
        return generateProductPdf(products, "Catalogue Produits");
    }

    // ─── Méthodes privées de génération PDF ───────────────────────

    private byte[] generateOrderPdf(List<OrderDTO> orders, String title)
            throws JRException {

        InputStream stream = getClass().getResourceAsStream("/reports/orders.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);

        // Paramètres du rapport (titre, date...)
        Map<String, Object> params = new HashMap<>();
        params.put("reportTitle", title);
        params.put("generatedDate", java.time.LocalDateTime.now().toString());

        // Source de données : liste des commandes
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);

        // Remplir et exporter en PDF
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] generateProductPdf(List<ProductDTO> products, String title)
            throws JRException {

        InputStream stream = getClass().getResourceAsStream("/reports/products.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);

        Map<String, Object> params = new HashMap<>();
        params.put("reportTitle", title);
        params.put("generatedDate", java.time.LocalDateTime.now().toString());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
