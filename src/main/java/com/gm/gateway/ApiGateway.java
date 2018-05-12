package com.gm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ApiGateway {

    public static void main(String[] args) {
        SpringApplication.run(ApiGateway.class, args);
    }


    @RequestMapping("/hystrixfallback")
    public String hystrixfallback() {
        return "This is a gateway fallback situation";
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                .route("mocker_get_route", r -> r.path("/getmockerscenario/**")
                        .filters(f -> f.rewritePath("/getmockerscenario/(?<segment>.*)", "/mocker/view/${segment}")
                                .hystrix(c -> c.setName("I_AM_TOO_SLOW")
                                        .setFallbackUri("forward:/hystrixfallback")))
                        .uri("http://localhost:8090/mocker/view/scenario/"))
                .build();
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http.httpBasic().and()
                .authorizeExchange()
                .pathMatchers("/anything/**").authenticated()
                .anyExchange().permitAll()
                .and()
                .build();
    }


}
