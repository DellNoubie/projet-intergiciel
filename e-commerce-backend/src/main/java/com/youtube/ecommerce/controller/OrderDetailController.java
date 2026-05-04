package com.youtube.ecommerce.controller;

import com.youtube.ecommerce.entity.*;
import com.youtube.ecommerce.service.OrderDetailService;
import com.youtube.ecommerce.service.ProductService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasRole('User')")
    @PostMapping({"/placeOrder/{isSingleProductCheckout}"})
    public void placeOrder(@PathVariable(name = "isSingleProductCheckout") boolean isSingleProductCheckout,
            @RequestBody OrderInput orderInput) {
        orderDetailService.placeOrder(orderInput, isSingleProductCheckout);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping({"/getOrderDetails"})
    public List<OrderDetail> getOrderDetails() {
        return orderDetailService.getOrderDetails();
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping({"/getAllOrderDetails/{status}"})
    public List<OrderDetail> getAllOrderDetails(@PathVariable(name = "status") String status) {
        return orderDetailService.getAllOrderDetails(status);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping({"/markOrderAsDelivered/{orderId}"})
    public void markOrderAsDelivered(@PathVariable(name = "orderId") Integer orderId) {
        orderDetailService.markOrderAsDelivered(orderId);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping({"/createTransaction/{amount}"})
    public TransactionDetails createTransaction(@PathVariable(name = "amount") Double amount) {
        return orderDetailService.createTransaction(amount);
    }

    @GetMapping("/pdfOrder")
    @ResponseBody
    public byte[] getCommandesPdf(HttpServletResponse response) throws JRException, IOException {
        byte[] bytes = null;

        List<OrderProduct> orders = orderDetailService.getOrderProductListd();
        // Génération du rapport Jaspersoft
        Report report = new Report();
        try {
            bytes = JasperExportManager.exportReportToPdf(report.generateReportOrder(orders));

            // Envoi du rapport au client
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);

        }catch (JRException e){
            e.printStackTrace();
        }
        return bytes;
    }

    @GetMapping("/pdfInvoice/{OrderId}")
    @ResponseBody
    public byte[] getFacturePdf(HttpServletResponse response, @PathVariable("OrderId") Integer id) throws JRException, IOException {
        byte[] bytes = null;

        List<OrderProduct> orders = orderDetailService.getOrderProductListd(id);
        // Génération du rapport Jaspersoft
        Report report = new Report();

        try {
            bytes = JasperExportManager.exportReportToPdf(report.generateReportInvoice(orders,id));

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
