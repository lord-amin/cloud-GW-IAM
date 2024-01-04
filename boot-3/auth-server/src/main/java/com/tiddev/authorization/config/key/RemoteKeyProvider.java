package com.tiddev.authorization.config.key;

import com.tiddev.authorization.remote.RemoteConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "rsa.key.provider.remote.enable", havingValue = "true")
@RequiredArgsConstructor
public class RemoteKeyProvider implements KeyProvider, InitializingBean {
    private final RemoteConfigService remoteConfigService;
    private Map<String, String> key;

    @Override
    public String privateKey() {
        if (key == null)
            key = remoteConfigService.getConfig();
        return key.get("private");
    }

    @Override
    public String publicKey() {
        if (key == null)
            key = remoteConfigService.getConfig();
        return key.get("public");
    }

    @Override
    public String kId() {
        if (key == null)
            key = remoteConfigService.getConfig();
        return key.get("kId");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("======================= key provider =============================");
        log.info("                        remote provider");
        log.info("==================================================================");
    }
}
