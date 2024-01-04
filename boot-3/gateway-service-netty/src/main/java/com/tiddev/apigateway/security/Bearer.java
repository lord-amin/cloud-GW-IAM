package com.tiddev.apigateway.security;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

public class Bearer implements ServerAuthenticationEntryPoint {

    private String realmName;

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authException) {
        return Mono.defer(() -> {
            HttpStatus status = getStatus(authException);
            Map<String, String> parameters = createParameters(authException);
            String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
            response.setStatusCode(status);
//            return error(response,"Failed auth".getBytes());
            return response.setComplete();
        });
    }

    private Map<String, String> createParameters(AuthenticationException authException) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            parameters.put("error", error.getErrorCode());
            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }
            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }
            if (error instanceof BearerTokenError) {
                BearerTokenError bearerTokenError = (BearerTokenError) error;
                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
            }
        }
        return parameters;
    }

    private HttpStatus getStatus(AuthenticationException authException) {
        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            if (error instanceof BearerTokenError) {
                return ((BearerTokenError) error).getHttpStatus();
            }
        }
        return HttpStatus.UNAUTHORIZED;
    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
    }

    public Mono<Void> error(ServerHttpResponse response, byte[] body) {
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        return response.writeWith(Mono.just(body).map(dataBufferFactory::wrap));
    }
}