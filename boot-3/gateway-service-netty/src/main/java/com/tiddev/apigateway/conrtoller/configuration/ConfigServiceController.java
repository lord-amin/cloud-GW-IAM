package com.tiddev.apigateway.conrtoller.configuration;

import com.tiddev.apigateway.service.remote.client.config.RemoteConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
/**
 * @author Yaser(amin) Sadeghi
 */
@Slf4j
@RequiredArgsConstructor
@RestController("/api")
public class ConfigServiceController {
    private final RemoteConfigService configService;

    @GetMapping("/protected/test")
    public Mono<Map<String, String>> getConfigFor() {
        log.info("Trying to send ");
        return configService.getConfig();
    }

}
