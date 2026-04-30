package com.ali.personcourseservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity

// ① Enables @PreAuthorize annotations in controllers
// prePostEnabled = true activates hasRole() and hasAnyRole()
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // ② Name of your Keycloak realm
    // Must match exactly what you created in Keycloak
    private static final String REALM_NAME = "school-realm";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // ③ Disable CSRF — not needed for stateless REST APIs
            // CSRF protects browser-based session apps, not JWT APIs
            .csrf().disable()

            // ④ STATELESS session — never create HTTP sessions
            // Every request must carry its own JWT token
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            // ⑤ Define which endpoints need authentication
            .authorizeHttpRequests(auth -> auth

                // Actuator health endpoint — always public
                // Eureka and Docker health checks need this
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/info").permitAll()

                // All API endpoints require authentication
                // Fine-grained control handled by @PreAuthorize
                .antMatchers("/api/**").authenticated()

                // Any other request also requires authentication
                .anyRequest().authenticated()
            )

            // ⑥ Configure as OAuth2 Resource Server with JWT
            // This tells Spring: "validate incoming Bearer tokens"
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    // ⑦ JWT Authentication Converter
    // Extracts roles from Keycloak JWT token structure
    // and converts them to Spring Security authorities
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // ⑧ Tell Spring WHERE to find roles inside the JWT token
        // Keycloak puts roles inside: realm_access.roles
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            // ⑨ Extract realm_access claim from token
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }

            // ⑩ Extract roles list from realm_access
            Collection<String> roles = (Collection<String>)
                    realmAccess.get("roles");

            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }

            // ⑪ Convert each role to Spring Security authority
            // IMPORTANT: Spring Security expects "ROLE_" prefix
            // So Keycloak role "ADMIN" becomes "ROLE_ADMIN"
            // This makes hasRole('ADMIN') work in @PreAuthorize
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });

        return converter;
    }
}