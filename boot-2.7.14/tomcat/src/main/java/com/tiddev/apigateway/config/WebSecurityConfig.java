package com.tiddev.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

@Configuration
public class WebSecurityConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    // use for load balancing
//    @Bean
//    public WebClient.Builder webClientBuilder(ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction) {
//        return WebClient.builder().filter(loadBalancerExchangeFilterFunction);
//    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            AppConfig config,
                                                            TokenFinder tokenFinder,
                                                            ServerAuthenticationEntryPoint bearer
                                                            ) {
        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchange = http.cors().and().csrf().disable().authorizeExchange();
        if (Objects.nonNull(config.getSecurity()) && Objects.nonNull(config.getSecurity().getIgnorePathList())
                && config.getSecurity().getIgnorePathList().length > 0) {
            Arrays.stream(config.getSecurity().getIgnorePathList()).forEach(s -> LOGGER.warn("The unsecure resource is        {}", s));
            LOGGER.warn("{}", System.lineSeparator());
            authorizeExchange.pathMatchers(config.getSecurity().getIgnorePathList()).permitAll();
        }
        // a filter for logout token handling
        http.addFilterAfter(new TokenBlackListFilter(tokenFinder, bearer), SecurityWebFiltersOrder.CSRF);
        authorizeExchange
                .anyExchange()
                .authenticated()
                .and()
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.authenticationEntryPoint(bearer).jwt());
        return http.build();
    }

    // redefine for setting web client with load balancer for jwk-set-uri
    @Bean
    @ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri")
    ReactiveJwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties, WebClient.Builder webClient,
                                  ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction) {
        webClient.filter(loadBalancerExchangeFilterFunction);
        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(properties.getJwt().getJwkSetUri())
                .webClient(webClient.build())
                .jwsAlgorithms(signatureAlgorithms -> jwsAlgorithms(signatureAlgorithms, properties))
                .build();
        String issuerUri = properties.getJwt().getIssuerUri();
        OAuth2TokenValidator<Jwt> defaultValidator = (issuerUri != null)
                ? JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators.createDefault();
        nimbusReactiveJwtDecoder.setJwtValidator(getValidators(defaultValidator, properties));
        return nimbusReactiveJwtDecoder;
    }

    private void jwsAlgorithms(Set<SignatureAlgorithm> signatureAlgorithms, OAuth2ResourceServerProperties properties) {
        for (String algorithm : properties.getJwt().getJwsAlgorithms()) {
            signatureAlgorithms.add(SignatureAlgorithm.from(algorithm));
        }
    }

    private OAuth2TokenValidator<Jwt> getValidators(OAuth2TokenValidator<Jwt> defaultValidator, OAuth2ResourceServerProperties properties) {
        List<String> audiences = properties.getJwt().getAudiences();
        if (CollectionUtils.isEmpty(audiences)) {
            return defaultValidator;
        }
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(defaultValidator);
        validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                (aud) -> aud != null && !Collections.disjoint(aud, audiences)));
        return new DelegatingOAuth2TokenValidator<>(validators);
    }

    @Bean
    public ServerAuthenticationConverter serverBearerTokenAuthenticationConverter() {
        return new ServerBearerTokenAuthenticationConverter();
    }

    @Bean
    public ServerAuthenticationEntryPoint bearer() {
        return new Bearer();
    }


    public static class Bearer implements ServerAuthenticationEntryPoint {

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
}
