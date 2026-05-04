package com.ecommerce.report.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer orderId;
    private String userEmail;
    private Integer productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private String orderAddress;
    private String orderStatus;
    private Double orderAmount;
    private LocalDateTime orderDate;
}
