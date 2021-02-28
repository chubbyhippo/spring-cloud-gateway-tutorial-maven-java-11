package com.example.basics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class BasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicsApplication.class, args);
    }

    @Bean
    ApplicationListener<RefreshRoutesResultEvent> routesRefreshed() {
        return new ApplicationListener<RefreshRoutesResultEvent>() {
            @Override
            public void onApplicationEvent(RefreshRoutesResultEvent refreshRoutesResultEvent) {
                var cachingRouteLocator = (CachingRouteLocator) refreshRoutesResultEvent.getSource();
                Flux<Route> routes = cachingRouteLocator.getRoutes();
                routes.subscribe(System.out::println);
            }
        };
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(routeSpec -> routeSpec
                        .path("/customers")
                        .uri("lb://customers"))
                .build();
    }
}
