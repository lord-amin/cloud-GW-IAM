package com.tiddev.authorization.config.key;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LocalKeyProvider implements KeyProvider {
    private final RSAConfig rsaConfig;

    @Override
    public String privateKey() {
        return rsaConfig.getPrivateKey();
    }

    @Override
    public String publicKey() {
        return rsaConfig.getPublicKey();
    }

    @Override
    public String kId() {
        return rsaConfig.getKid();
    }
}
