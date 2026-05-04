package com.ecommerce.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ORDER-SERVICE", fallback = OrderClientFallback.class)
public interface OrderClient {

    @GetMapping("/orders/admin/all")
    List<OrderDTO> getAllOrders();

    @GetMapping("/orders/{id}")
    OrderDTO getOrderById(@PathVariable("id") Integer orderId);

    @GetMapping("/orders/my")
    List<OrderDTO> getMyOrders();
}
