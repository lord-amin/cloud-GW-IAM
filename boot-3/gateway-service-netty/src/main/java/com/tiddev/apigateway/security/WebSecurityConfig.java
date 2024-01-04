package com.tiddev.apigateway.security;

import com.tiddev.apigateway.config.AppConfig;
import com.tiddev.apigateway.service.remote.client.config.RemoteConfigService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.JwkSetUriReactiveJwtDecoderBuilderCustomizer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;

@Configuration
public class WebSecurityConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${server.port}")
    private int port;

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            LOGGER.info("The request for {} -> {}", exchange.getRequest().getRemoteAddress(), exchange.getRequest().getURI());
            if (!HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                return chain.filter(exchange);
            }
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            LOGGER.info("The request for {}:{} with method {}", port, request.getURI(), request.getMethod());
            prepareCorsHeaders(response);
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };
    }

    private void prepareCorsHeaders(ServerHttpResponse response) {
        LOGGER.warn("Options request from ");
        HttpHeaders headers = response.getHeaders();
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowMethods(Arrays.asList(HttpMethod.values()));
        headers.setAccessControlMaxAge(3600);
        headers.setAccessControlAllowHeaders(Collections.singletonList("*"));
    }

    @Bean
    public RegistryEventConsumer<CircuitBreaker> myCircuitBreakerRegistryEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                LOGGER.info("ADDED type {} ==> {}", entryAddedEvent.getEventType(), entryAddedEvent);
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
                LOGGER.info("REMOVED type {} ==> {}", entryRemoveEvent.getEventType(), entryRemoveEvent);
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                LOGGER.info(" REPLACE OLD type {} ==> {}", entryReplacedEvent.getEventType(), entryReplacedEvent.getOldEntry());
                LOGGER.info(" REPLACE NEW type {} ==> {}", entryReplacedEvent.getEventType(), entryReplacedEvent.getNewEntry());
            }
        };
    }

    @Bean
    public SecurityWebFilterChain configureResourceServer(ServerHttpSecurity httpSecurity,
                                                          AppConfig config,
                                                          TokenFinder tokenFinder,
                                                          ServerAuthenticationEntryPoint bearer,
                                                          RemoteConfigService remoteConfigService
    )  {
        return httpSecurity.addFilterAfter(new TokenBlackListFilter(tokenFinder, bearer
                        , remoteConfigService
                ), SecurityWebFiltersOrder.CSRF).cors(ServerHttpSecurity.CorsSpec::disable).csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> {
                    if (Objects.nonNull(config.getSecurity()) && Objects.nonNull(config.getSecurity().getIgnorePathList())
                            && config.getSecurity().getIgnorePathList().length > 0) {
                        authorizeExchangeSpec
                                .pathMatchers(config.getSecurity().getIgnorePathList()).permitAll().anyExchange().authenticated();
                    } else {
                        authorizeExchangeSpec.anyExchange().authenticated();
                    }
                }).oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec -> {
                })).build();
    }

    @Bean
    public ServerAuthenticationEntryPoint bearer() {
        return new Bearer();
    }

//    @Bean
//    public WebClient webClient(WebClient.Builder webClientBuilder) {
//        return webClientBuilder.build();
//    }

//    @Bean
//    @Scope("prototype")
//    public WebClient.Builder webClientBuilder(ObjectProvider<WebClientCustomizer> customizerProvider,
//                                              ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction) {
//        WebClient.Builder builder = WebClient.builder();
//        customizerProvider.orderedStream().forEach((customizer) -> {
//            customizer.customize(builder);
//        });
//        builder.filter(loadBalancerExchangeFilterFunction);
//        return builder;
//    }

    @Bean
    @ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri")
    ReactiveJwtDecoder jwtDecoder(ObjectProvider<JwkSetUriReactiveJwtDecoderBuilderCustomizer> customizers,
                                  OAuth2ResourceServerProperties properties,
                                  WebClient.Builder webClient,
                                  ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction) {
        webClient.filter(loadBalancerExchangeFilterFunction);
        NimbusReactiveJwtDecoder.JwkSetUriReactiveJwtDecoderBuilder builder = NimbusReactiveJwtDecoder
                .withJwkSetUri(properties.getJwt().getJwkSetUri())
                .webClient(webClient.build())
                .jwsAlgorithms(signatureAlgorithms -> jwsAlgorithms(signatureAlgorithms, properties.getJwt()));
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = builder.build();
        String issuerUri = properties.getJwt().getIssuerUri();
        Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = (issuerUri != null)
                ? () -> JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators::createDefault;
        nimbusReactiveJwtDecoder.setJwtValidator(getValidators(defaultValidator, properties.getJwt()));
        return nimbusReactiveJwtDecoder;
    }

    private void jwsAlgorithms(Set<SignatureAlgorithm> signatureAlgorithms, OAuth2ResourceServerProperties.Jwt jwt) {
        for (String algorithm : jwt.getJwsAlgorithms()) {
            signatureAlgorithms.add(SignatureAlgorithm.from(algorithm));
        }
    }

    private OAuth2TokenValidator<Jwt> getValidators(Supplier<OAuth2TokenValidator<Jwt>> defaultValidator, OAuth2ResourceServerProperties.Jwt jwt) {
        OAuth2TokenValidator<Jwt> defaultValidators = defaultValidator.get();
        List<String> audiences = jwt.getAudiences();
        if (CollectionUtils.isEmpty(audiences)) {
            return defaultValidators;
        }
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(defaultValidators);
        validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                (aud) -> aud != null && !Collections.disjoint(aud, audiences)));
        return new DelegatingOAuth2TokenValidator<>(validators);
    }

}
