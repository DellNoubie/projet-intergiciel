package com.ecommerce.report.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderClientFallback implements OrderClient {

    private static final Logger log = LoggerFactory.getLogger(OrderClientFallback.class);

    @Override
    public List<OrderDTO> getAllOrders() {
        log.error("ORDER-SERVICE indisponible — getAllOrders fallback");
        return Collections.emptyList();
    }

    @Override
    public OrderDTO getOrderById(Integer orderId) {
        log.error("ORDER-SERVICE indisponible — getOrderById({}) fallback", orderId);
        return null;
    }

    @Override
    public List<OrderDTO> getMyOrders() {
        log.error("ORDER-SERVICE indisponible — getMyOrders fallback");
        return Collections.emptyList();
    }
}
