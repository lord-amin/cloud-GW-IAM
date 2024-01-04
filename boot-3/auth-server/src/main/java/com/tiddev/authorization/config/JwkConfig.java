package com.tiddev.authorization.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.tiddev.authorization.config.key.KeyProvider;
import com.tiddev.authorization.config.key.LocalKeyProvider;
import com.tiddev.authorization.config.key.RSAConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({RSAConfig.class})
public class JwkConfig {

    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyProvider keyProvider) {
        KeyPair keyPair = jwtKeyPair(keyProvider);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        log.warn("The kid for auth server is {}", keyProvider.kId());
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyProvider.kId())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }


    public KeyPair jwtKeyPair(KeyProvider keyProvider) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(keyProvider.privateKey().getBytes(StandardCharsets.UTF_8));
            byte[] publicKeyBytes = Base64.getDecoder().decode(keyProvider.publicKey().getBytes(StandardCharsets.UTF_8));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean(KeyProvider.class)
    public KeyProvider localKeyProvider(RSAConfig rsaConfig) {
        log.info("======================= key provider =============================");
        log.info("                        local provider");
        log.info("==================================================================");
        return new LocalKeyProvider(rsaConfig);
    }
}
