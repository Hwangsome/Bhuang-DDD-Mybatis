package com.ecommerce.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器 - API Gateway统一认证
 * 职责：验证JWT Token，提取用户信息，控制访问权限
 * 特性：白名单机制、Token验证、用户信息注入、友好错误响应
 */
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.header:Authorization}")
    private String jwtHeader;
    
    @Value("${jwt.prefix:Bearer }")
    private String jwtPrefix;
    
    @Value("${gateway.whitelist.paths:}")
    private List<String> whitelistPaths;
    
    private final ObjectMapper objectMapper;
    
    public AuthGatewayFilterFactory(ObjectMapper objectMapper) {
        super(Config.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            
            // 检查是否在白名单中
            if (isWhitelisted(path)) {
                return chain.filter(exchange);
            }
            
            // 提取Token
            String token = extractToken(exchange.getRequest().getHeaders());
            if (!StringUtils.hasText(token)) {
                return handleUnauthorized(exchange, "缺少认证Token");
            }
            
            try {
                // 验证Token并提取用户信息
                Claims claims = validateToken(token);
                String userId = claims.getSubject();
                String username = claims.get("username", String.class);
                String userType = claims.get("userType", String.class);
                
                // 将用户信息添加到请求头中，传递给下游服务
                exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
                        .header("X-User-Type", userType)
                        .build();
                
                return chain.filter(exchange);
                
            } catch (Exception e) {
                return handleUnauthorized(exchange, "Token验证失败: " + e.getMessage());
            }
        };
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        if (whitelistPaths == null) {
            return false;
        }
        
        return whitelistPaths.stream()
                .anyMatch(whitelistPath -> {
                    if (whitelistPath.endsWith("/**")) {
                        String prefix = whitelistPath.substring(0, whitelistPath.length() - 3);
                        return path.startsWith(prefix);
                    } else if (whitelistPath.endsWith("/*")) {
                        String prefix = whitelistPath.substring(0, whitelistPath.length() - 2);
                        return path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
                    } else {
                        return path.equals(whitelistPath);
                    }
                });
    }

    /**
     * 提取JWT Token
     */
    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(jwtHeader);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(jwtPrefix)) {
            return authHeader.substring(jwtPrefix.length());
        }
        return null;
    }

    /**
     * 验证JWT Token
     */
    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 处理未授权请求
     */
    private Mono<Void> handleUnauthorized(org.springframework.web.server.ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", 401);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", exchange.getRequest().getPath().value());
        
        try {
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }

    /**
     * 过滤器配置类
     */
    public static class Config {
        // 可以添加配置参数
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}