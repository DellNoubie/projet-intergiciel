package com.ecommerce.report.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductClientFallback implements ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClientFallback.class);

    @Override
    public List<ProductDTO> getAllProducts() {
        log.error("PRODUCT-SERVICE indisponible — getAllProducts fallback");
        return Collections.emptyList();
    }
}
