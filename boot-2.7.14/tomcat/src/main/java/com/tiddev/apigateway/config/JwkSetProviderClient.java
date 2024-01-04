//package com.tiddev.apigateway.config;
//
//import com.nimbusds.jose.jwk.JWKSet;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@FeignClient(name = "authorization-service")
//public interface JwkSetProviderClient {
//
//    @GetMapping("/oauth2/jwks")
//    JWKSet getJwkSet();
//}