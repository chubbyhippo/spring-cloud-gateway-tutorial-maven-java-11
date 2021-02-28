package com.example.basics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class BasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicsApplication.class, args);
    }

    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Bean
    @RefreshScope
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        var id = "customer";
        if (!this.atomicBoolean.get()) {
            this.atomicBoolean.set(true);
            return routeLocatorBuilder.routes()
                    .route(id, rs -> rs.path("/customers")
                            .uri("lb://customers")
                    )
                    .build();
        } else {

            return routeLocatorBuilder.routes()
                    .route(id, rs -> rs.path("/customers")
                            .filters(gatewayFilterSpec -> gatewayFilterSpec.setPath(
                                    "/ws/customers"
                            ))
                            .uri("lb://customers")
                    )
                    .build();
        }

    }


    @Bean
    ApplicationListener<RefreshRoutesResultEvent> routesRefreshed() {
        return refreshRoutesResultEvent -> {
            System.out.println("routes updated");
            var cachingRouteLocator = (CachingRouteLocator) refreshRoutesResultEvent.getSource();
            var routes = cachingRouteLocator.getRoutes();
            routes.subscribe(System.out::println);
        };
    }

    @Bean
    RouteLocator gateway(SetPathGatewayFilterFactory setPathGatewayFilterFactory) {
        var singleRoute = Route.async()
                .id("test-route")
                .filter(new OrderedGatewayFilter(setPathGatewayFilterFactory.apply(
                        config -> config.setTemplate("/customers")

                ), 1))
                .uri("lb://customers")
                .asyncPredicate(serverWebExchange -> {
                    var uri = serverWebExchange.getRequest().getURI();
                    var path = uri.getPath();
                    var match = path.contains("/customers");
                    return Mono.just(match);
                }).build();

        return () -> Flux.just(singleRoute);
    }
}
