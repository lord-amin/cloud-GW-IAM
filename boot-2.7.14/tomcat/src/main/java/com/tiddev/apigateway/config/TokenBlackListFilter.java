package com.tiddev.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

//@Component
@Slf4j
public class TokenBlackListFilter implements WebFilter {
    private final List<String> blackListToken = new ArrayList<>();
    private final TokenFinder tokenFinder;
    {
        {
            blackListToken.add("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwa09ZX0VaclozbEM5VjNIdkxWRjd0M0JSMnpjQl9SNlVBSEp5VnJEU0JRIn0.eyJleHAiOjE2OTc2MjI2NzUsImlhdCI6MTY5NzYxNjY3NSwianRpIjoiYWJiMzZhYjUtYTNhMC00NTMwLTlhODAtYTQ2YWYxNTdiMzdhIiwiaXNzIjoiaHR0cHM6Ly8xOTIuMTY4LjEwMi44Mjo4MDgxL3JlYWxtcy9tYnAiLCJzdWIiOiIzYzliMjBlMy1hY2I5LTRhYmMtOWIyOS1jMDNhNGFiZTViZGMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiIxMTEwMDAwMDI0Iiwic2NvcGUiOiJvcGVuaWQgTUJfUF9BRE1JTiIsImNsaWVudEhvc3QiOiIxOTIuMTY4LjQwLjkyIiwiY2xpZW50QWRkcmVzcyI6IjE5Mi4xNjguNDAuOTIiLCJjbGllbnRfaWQiOiIxMTEwMDAwMDI0In0.N3Grx_3KasVGq5q8yUYxdBiLgH4pgbKf3eI8ztVV5F1to86qZ4x2ZHo0hAJnK_7ZCZpGmpQpkHgqEIMaAZccPgblyHpswKR4LVLy1cFHJSpjfA-SYCV7ERLcEHLgZjJmVl4kHwpV2zCreBy7W5bHPrMScEQAPJ_yH7p7LExw5Cd_BVZLqz_jFnGV6mX3fhxT3BAr1bJCO0p5uRP8hx_4FJm1nBebU-_hFzHkZyLlu86XcEVTMyZyQXZl4I9KlQ7M4MQsIu2cdN3mo_ycvoi087cRWb9N9DiRGrXAvIo1px10mZdiuuTUKDvNbsLAD7mTJyKuct2VbgBuRSkzaQ3yIg");
        }
    }

    private final ServerAuthenticationEntryPoint bearer;

    public TokenBlackListFilter(TokenFinder tokenFinder, ServerAuthenticationEntryPoint bearer) {
        this.tokenFinder = tokenFinder;
        this.bearer = bearer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.warn("Entering in the token black list filter ");
        String token = tokenFinder.token(exchange.getRequest());
//        return chain.filter(exchange);
        if (ObjectUtils.isEmpty(token)) {
            log.warn("Token not found ...");
            return chain.filter(exchange);
        }
        if (blackListToken.contains(token)) {
            log.warn("Token is in black list");
            return bearer.commence(exchange, new OAuth2AuthenticationException("Token is in black list"));
        } else {
            log.warn("Token is not in black list");
            return chain.filter(exchange);
        }
    }
}