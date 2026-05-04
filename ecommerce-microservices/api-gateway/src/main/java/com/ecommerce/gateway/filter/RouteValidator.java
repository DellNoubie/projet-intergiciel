package com.ecommerce.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    /**
     * Endpoints publics — pas besoin de JWT.
     * IMPORTANT : les routes Swagger des services (/xxx-service/api-docs)
     * doivent être ici sinon la gateway bloque le fetch des specs OpenAPI.
     */
    public static final List<String> OPEN_ENDPOINTS = List.of(
            "/authenticate",
            "/registerNewUser",
            "/products",
            "/eureka",
            // Swagger UI gateway
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars/swagger-ui",
            "/api-docs",
            "/v3/api-docs",
            // Swagger docs de chaque service (agrégés via gateway)
            "/auth-service/api-docs",
            "/product-service/api-docs",
            "/order-service/api-docs",
            "/payment-service/api-docs",
            "/report-service/api-docs"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_ENDPOINTS.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}