package com.example.basics;

import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class BasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicsApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(routeSpec -> routeSpec
                        .path("/hello")
                        .and()
                        .host("*.spring.io")
                        .filters(gatewayFilterSpec ->
                                gatewayFilterSpec.setPath("/guides") // change /hello to /guides
                        )
                        .uri("https://spring.io/")
                )
                .route("twitter", routeSpec ->
                        routeSpec
                                .path("/twitter/**")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath(
                                        "/twitter/(?<handle>.*)",
                                        "/${handle}"

                                ))
                                .uri("http://twitter.com/@")
                )
                .build();
    }

}
