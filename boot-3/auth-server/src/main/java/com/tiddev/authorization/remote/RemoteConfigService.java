package com.tiddev.authorization.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Yaser(amin) Sadeghi
 */
@FeignClient("config-service")
public interface RemoteConfigService {
    @GetMapping("/api/key")
    Map<String,String> getConfig();
}
