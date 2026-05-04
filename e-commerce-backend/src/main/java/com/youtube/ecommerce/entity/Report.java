package com.youtube.ecommerce.entity;

import com.youtube.ecommerce.dao.OrderDetailDao;
import com.youtube.ecommerce.repository.OrderDetailRepository;
import com.youtube.ecommerce.service.OrderDetailService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Report {

    @Autowired
    private OrderDetailService orderService;
    @Autowired
    private OrderDetailRepository order;
    @Autowired
    private OrderDetailDao orderDetailDao;
    public JasperPrint generateReport(List<Product> clients) throws JRException  {
        JasperReport report = null;
        // Création du rapport Jaspersoft
        try {
            report = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/reports/Product_Coffee.jrxml"));
        }catch (FileNotFoundException e) {
            e.getMessage();
        }

        // Création de la source de données
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clients);

        // Remplissage du rapport
        JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);
        //create a folder to store the report
        String fileName = "/"+"products.pdf";
        try {
            Path uploadPath = getUploadPath( print, fileName);
        }catch (FileNotFoundException e) {
            e.getMessage();
        }
        System.out.println("Upload");
        return print;
    }

    public List<OrderDetail> getAllOrderDetail() {
        Pageable pageable = PageRequest.of(0,12);
        return (List<OrderDetail>) orderDetailDao.findAll(pageable);
    }

    public List<OrderProduct> getOrderProductListd() {
        List<OrderProduct> orderProductList = new ArrayList<>();
        List<OrderDetail> orderDetails = getAllOrderDetail();
        for (OrderDetail orderDetail : orderDetails) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductName(orderDetail.getProduct().getProductName());
            orderProduct.setOrderAmount(orderDetail.getOrderAmount());
            orderProduct.setOrderContactNumber(orderDetail.getOrderContactNumber());
            orderProduct.setOrderFullName(orderDetail.getOrderFullName());
            orderProduct.setOrderFullOrder(orderDetail.getOrderFullOrder());
            orderProduct.setOrderStatus(orderDetail.getOrderStatus());
            orderProduct.setTransactionId(orderDetail.getTransactionId());
            orderProduct.setTime(orderDetail.getTime());
            orderProductList.add(orderProduct);
        }
        System.out.println("liste de donnees: " + orderProductList);
        return orderProductList;

    }

    public JasperPrint generateReportOrder(List<OrderProduct> orderProduct) throws JRException  {
        JasperReport report = null;
        // Création du rapport Jaspersoft
        try {
            report = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/reports/Commandes_Coffee_Landscape.jrxml"));
        }catch (FileNotFoundException e) {
            e.getMessage();
        }

        // Création de la source de données
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orderProduct);

        // Remplissage du rapport
        JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);
        //create a folder to store the report
        String fileName = "/"+"products.pdf";
        try {
            Path uploadPath = getUploadPath( print, fileName);
        }catch (FileNotFoundException e) {
            e.getMessage();
        }
        System.out.println("Upload");
        return print;
    }

    public JasperPrint generateReportInvoice(List<OrderProduct> orderProduct, Integer id) throws JRException  {
        JasperReport report = null;
        // Création du rapport Jaspersoft
        try {
            report = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/reports/Facture_Coffee.jrxml"));
        }catch (FileNotFoundException e) {
            e.getMessage();
        }

        // Création de la source de données
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orderProduct);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("Order_ID",id);

        // Remplissage du rapport
        JasperPrint print = JasperFillManager.fillReport(report,params , dataSource);
        //create a folder to store the report
        String fileName = "/"+"products.pdf";
        try {
            Path uploadPath = getUploadPath( print, fileName);
        }catch (FileNotFoundException e) {
            e.getMessage();
        }
        System.out.println("Upload");
        return print;
    }

    private Path getUploadPath( JasperPrint print, String fileName) throws FileNotFoundException, JRException {
        String uploadDir = StringUtils.cleanPath("./generated-reports");
        Path uploadPath = Paths.get(uploadDir);
        return uploadPath;
    }

    private String getPdfFileLink(String uploadPath){
        return uploadPath+"/"+"products.pdf";
    }


}

