package com.tiddev.authorization.config.key;

public interface KeyProvider {
    String privateKey();

    String publicKey();

    String kId();
}
