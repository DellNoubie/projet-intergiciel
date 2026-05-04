package com.ecommerce.payment.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor authHeaderInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String user = request.getHeader("X-Auth-User");
                String role = request.getHeader("X-Auth-Role");
                if (user != null && !user.isBlank()) requestTemplate.header("X-Auth-User", user);
                if (role != null && !role.isBlank()) requestTemplate.header("X-Auth-Role", role);
            }
        };
    }
}
