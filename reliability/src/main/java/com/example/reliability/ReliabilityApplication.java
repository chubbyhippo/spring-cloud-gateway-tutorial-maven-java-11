package com.example.reliability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReliabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReliabilityApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(predicateSpec -> predicateSpec
                        .path("/error/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(5))
                        .uri("lb://customers")
                ).build()
    }


}
