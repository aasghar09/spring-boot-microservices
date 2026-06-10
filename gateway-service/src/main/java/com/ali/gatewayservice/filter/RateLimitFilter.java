package com.ali.gatewayservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${rate-limit.replenish-rate:10}")
    private int replenishRate;

    @Value("${rate-limit.burst-capacity:5}")
    private int burstCapacity;

    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        String key = resolveKey(exchange);
        String redisKey = "rate_limit:" + key;

        return redisTemplate.opsForValue().get(redisKey)
            .defaultIfEmpty("0")
            .flatMap(currentStr -> {
                int current = Integer.parseInt(currentStr);
                log.info("RateLimitFilter — key: {}, current count: {}", redisKey, current);

                if (current >= burstCapacity) {
                    log.warn("Rate limit exceeded for key: {}", key);
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    exchange.getResponse().getHeaders()
                        .add("X-RateLimit-Limit", String.valueOf(burstCapacity));
                    exchange.getResponse().getHeaders()
                        .add("X-RateLimit-Remaining", "0");
                    exchange.getResponse().getHeaders()
                        .add("Retry-After", "1");
                    return exchange.getResponse().setComplete();
                }

                return redisTemplate.opsForValue()
                    .increment(redisKey)
                    .flatMap(newCount -> {
                        log.info("RateLimitFilter — incremented to: {}", newCount);
                        return redisTemplate.expire(
                            redisKey,
                            java.time.Duration.ofSeconds(10)
                        ).flatMap(expired -> {
                            log.info("RateLimitFilter — expire result: {}", expired);
                            return chain.filter(exchange);
                        });
                    });
            })
            .onErrorResume(ex -> {
                log.error("RateLimitFilter error — bypassing rate limit: {}", ex.getMessage());
                return chain.filter(exchange);
            })
            .timeout(java.time.Duration.ofSeconds(3),
                Mono.defer(() -> {
                    log.warn("RateLimitFilter timeout — bypassing rate limit for key: {}", key);
                    return chain.filter(exchange);
                })
            );
    }

    private String resolveKey(ServerWebExchange exchange) {
        // Prefer JWT subject — fall back to IP address
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Extract subject from JWT payload (middle segment)
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payload = new String(
                        java.util.Base64.getUrlDecoder().decode(parts[1])
                    );
                    // Extract "sub" field from JSON payload
                    int subIndex = payload.indexOf("\"sub\":\"");
                    if (subIndex != -1) {
                        int start = subIndex + 7;
                        int end = payload.indexOf("\"", start);
                        return "user:" + payload.substring(start, end);
                    }
                }
            } catch (Exception e) {
                log.debug("Could not extract JWT subject — falling back to IP");
            }
        }

        // Fall back to IP address
        if (exchange.getRequest().getRemoteAddress() != null) {
            return "ip:" + exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }

        return "unknown";
    }

    @Override
    public int getOrder() {
        // Run BEFORE routing filters (order 1) but AFTER security (-1)
        return 0;
    }
}