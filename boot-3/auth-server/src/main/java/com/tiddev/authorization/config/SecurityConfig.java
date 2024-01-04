package com.tiddev.authorization.config;

import com.tiddev.authorization.client.controller.exception.BusinessException;
import com.tiddev.authorization.client.controller.exception.ExceptionControllerAdvice;
import com.tiddev.authorization.client.controller.exception.GeneralCodes;
import com.tiddev.authorization.client.controller.exception.ResponseDto;
import com.tiddev.authorization.client.domain.client.ClientDetailRepository;
import com.tiddev.authorization.client.service.AdvancedInMemoryJdbcRegisteredClientRepository;
import com.tiddev.authorization.client.service.MyJdbcRegisteredClientRepository;
import com.tiddev.authorization.client.service.SimpleInMemoryJdbcRegisteredClientRepository;
import com.tiddev.authorization.client.service.cache.CacheProvider;
import com.tiddev.authorization.client.service.cache.HeapCacheProvider;
import com.tiddev.authorization.client.service.cache.NoneCacheProvider;
import com.tiddev.authorization.client.service.cache.RedisCacheProvider;
import com.tiddev.authorization.event.RabbitTemplateDecorator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConfig {
    public static final AtomicLong REQUEST_RECEIVED = new AtomicLong();

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      OAuth2AccessTokenResponseHttpMessageConverter successHandler,
                                                                      MappingJackson2HttpMessageConverter jackson2HttpMessageConverter,
                                                                      ExceptionControllerAdvice exceptionControllerAdvice) throws Exception {
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .clientAuthentication(oAuth2ClientAuthenticationConfigurer -> oAuth2ClientAuthenticationConfigurer
                        .errorResponseHandler((request, response, exception) -> sendErrorResponse(exceptionControllerAdvice, jackson2HttpMessageConverter, response, exception)))
                .tokenEndpoint(oAuth2TokenEndpointConfigurer -> oAuth2TokenEndpointConfigurer
                        .accessTokenResponseHandler((request, response, authentication) -> sendAccessTokenResponse(successHandler, response, authentication))
                        .errorResponseHandler((request, response, exception) -> sendErrorResponse(exceptionControllerAdvice, jackson2HttpMessageConverter, response, exception)))
                .getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .with(authorizationServerConfigurer,oAuth2AuthorizationServerConfigurer -> {});
//                .apply(authorizationServerConfigurer);

//        http
//                .securityMatcher(endpointsMatcher)
//                .authorizeHttpRequests(authorize ->authorize.anyRequest().permitAll())
//                .csrf().disable();
//                .apply(authorizationServerConfigurer);
        return http.build();
    }

    @Bean
    public OAuth2AccessTokenResponseHttpMessageConverter oAuth2AccessTokenResponseHttpMessageConverter() {
        return new OAuth2AccessTokenResponseHttpMessageConverter();
    }

    @Bean
    public OAuth2ErrorHttpMessageConverter oAuth2ErrorHttpMessageConverter() {
        return new OAuth2ErrorHttpMessageConverter();
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            context.getClaims().claim("id", context.getRegisteredClient().getId());
        };
    }
    @ConditionalOnProperty(name = "cache.model", havingValue = "heap")
    @Bean
    public CacheProvider heapCacheProvider(RabbitTemplateDecorator rabbitTemplateDecorator,AppConfig appConfig){
        return new HeapCacheProvider(rabbitTemplateDecorator,appConfig);
    }
    @ConditionalOnProperty(name = "cache.model", havingValue = "redis")
    @Bean
    public CacheProvider redisCacheProvider(AppConfig appConfig){
        return new RedisCacheProvider(appConfig);
    }
    @ConditionalOnProperty(name = "cache.model", havingValue = "none")
    @Bean
    public CacheProvider noneCacheProvider() {
        return new NoneCacheProvider();
    }

    //    curl -iX POST http://192.168.102.82:8092/oauth2/token -d "client_id=gateway-client-id&client_secret=123456789&grant_type=client_credentials"
    @Bean
    public RegisteredClientRepository registeredClientRepository(@Value("${query.type:simple}") String cacheEnable,
                                                                 JdbcTemplate jdbcTemplate,
                                                                 ClientDetailRepository repository,
                                                                 CacheProvider cacheProvider) {
        if ("advanced".equalsIgnoreCase(cacheEnable))
            return new AdvancedInMemoryJdbcRegisteredClientRepository(jdbcTemplate, repository, cacheProvider);
        if ("simple".equalsIgnoreCase(cacheEnable))
            return new SimpleInMemoryJdbcRegisteredClientRepository(jdbcTemplate, repository, cacheProvider);
        return new MyJdbcRegisteredClientRepository(jdbcTemplate, repository);
    }

    @Bean
    public AuthorizationServerSettings providerSettings(@Value("${provider.url}") String pUrl) {
        AuthorizationServerSettings build = AuthorizationServerSettings.builder()
                .issuer(pUrl)
                .build();
        build.getSettings().forEach((key, value) -> log.warn("===================>  {} = {}", key, value));
        return build;
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new OAuth2AuthorizationService() {
            @Override
            public void save(OAuth2Authorization authorization) {
                log.info("Mocking OAuth2AuthorizationService save");
            }

            @Override
            public void remove(OAuth2Authorization authorization) {
                throw new AbstractMethodError("Mocking OAuth2AuthorizationService remove");
            }

            @Override
            public OAuth2Authorization findById(String id) {
                throw new AbstractMethodError("Mocking OAuth2AuthorizationService findById");
            }

            @Override
            public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
                throw new AbstractMethodError("Mocking OAuth2AuthorizationService findByToken");
            }
        };
    }

    private void sendErrorResponse(ExceptionControllerAdvice exceptionControllerAdvice,
                                   MappingJackson2HttpMessageConverter objectMapper,
                                   HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {

        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.FORBIDDEN);
        BusinessException ex = new BusinessException(GeneralCodes.FAILED, error.getDescription());
        ex.setMetadata(new HashMap<>());
        ex.getMetadata().put("reason", error.getDescription());
        ResponseEntity<ResponseDto<?, ?>> handle = exceptionControllerAdvice.handle(ex);
        objectMapper.write(handle.getBody(), MediaType.APPLICATION_JSON, httpResponse);
    }

    private void sendAccessTokenResponse(OAuth2AccessTokenResponseHttpMessageConverter converter, HttpServletResponse response,
                                         Authentication authentication) throws IOException {

        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
                (OAuth2AccessTokenAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        Set<String> scopes = accessTokenAuthentication.getRegisteredClient().getScopes();
        Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                        .tokenType(accessToken.getTokenType())
                        .scopes(scopes);
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        if (!CollectionUtils.isEmpty(additionalParameters)) {
            builder.additionalParameters(additionalParameters);
        }
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        converter.write(accessTokenResponse, MediaType.APPLICATION_JSON, httpResponse);
        REQUEST_RECEIVED.incrementAndGet();
    }


}
