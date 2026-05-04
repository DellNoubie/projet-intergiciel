package com.youtube.ecommerce.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Set;

public class OrderProduct {
    private String productName;
    private Double orderAmount;
    private String orderContactNumber;
    private String orderFullName;
    private String orderFullOrder;
    private String orderStatus;
    private String transactionId;
    private Date time;

    private String productDescription;
    private Double productActualPrice;
    private Set<ImageModel> productImages;
    private Double productDiscountedPrice;
    private Integer productId;

    // constructeurs, getters, setters


    public OrderProduct() {
    }

    public OrderProduct(String productName, Double orderAmount, String orderContactNumber, String orderFullName, String orderFullOrder, String orderStatus, String transactionId, Date time) {
        this.productName = productName;
        this.orderAmount = orderAmount;
        this.orderContactNumber = orderContactNumber;
        this.orderFullName = orderFullName;
        this.orderFullOrder = orderFullOrder;
        this.orderStatus = orderStatus;
        this.transactionId = transactionId;
        this.time = time;
    }


    public Double getProductActualPrice() {
        return productActualPrice;
    }

    public Set<ImageModel> getProductImages() {
        return productImages;
    }

    public Double getProductDiscountedPrice() {
        return productDiscountedPrice;
    }

    public void setProductActualPrice(Double productActualPrice) {
        this.productActualPrice = productActualPrice;
    }

    public void setProductImages(Set<ImageModel> productImages) {
        this.productImages = productImages;
    }

    public void setProductDiscountedPrice(Double productDiscountedPrice) {
        this.productDiscountedPrice = productDiscountedPrice;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductName() {
        return productName;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public String getOrderContactNumber() {
        return orderContactNumber;
    }

    public String getOrderFullName() {
        return orderFullName;
    }

    public String getOrderFullOrder() {
        return orderFullOrder;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getTime() {
        return time;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public void setOrderContactNumber(String orderContactNumber) {
        this.orderContactNumber = orderContactNumber;
    }

    public void setOrderFullName(String orderFullName) {
        this.orderFullName = orderFullName;
    }

    public void setOrderFullOrder(String orderFullOrder) {
        this.orderFullOrder = orderFullOrder;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

