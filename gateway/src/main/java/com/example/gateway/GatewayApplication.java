package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().permitAll();
        http.csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }
    /**
     * add csrf cookie response on every response
     * @return
     */
    @Bean
    public WebFilter addCsrfTokenFilter() {
        return (exchange, next) -> Mono.just(exchange)
                .flatMap(ex -> ex.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName()))
                .doOnNext(ex -> {
                })
                .then(next.filter(exchange));
    }


}
