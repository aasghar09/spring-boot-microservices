package com.ali.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

// ① @EnableWebFluxSecurity — NOT @EnableWebSecurity
// Gateway uses reactive WebFlux stack
// Using wrong annotation causes startup failure
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http) {

        http
            // ② Disable CSRF — stateless JWT API
            .csrf().disable()

            // ③ Authorize requests
            .authorizeExchange(exchanges -> exchanges

                // Actuator endpoints — public
                .pathMatchers("/actuator/**").permitAll()

                // All API routes — must be authenticated
                // Fine-grained role control handled downstream
                // by person-course-service @PreAuthorize
                .anyExchange().authenticated()
            )

            // ④ JWT Resource Server — reactive version
            .oauth2ResourceServer(ServerHttpSecurity
                .OAuth2ResourceServerSpec::jwt);

        return http.build();
    }
}