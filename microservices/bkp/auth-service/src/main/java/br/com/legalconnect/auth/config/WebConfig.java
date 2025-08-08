package br.com.legalconnect.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.server.WebFilter;

public class WebConfig {
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    public WebConfig(JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

    @Bean
    public WebFilter jwtAuthEntryPointFilter() {
        return jwtAuthEntryPoint;
    }

    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.getResponse().getHeaders().add("Access-Control-Max-Age", "3600");

            if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }
}