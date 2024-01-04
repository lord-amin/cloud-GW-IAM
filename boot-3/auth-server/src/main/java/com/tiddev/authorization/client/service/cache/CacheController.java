package com.tiddev.authorization.client.service.cache;

import com.tiddev.authorization.config.AppConfig;
import com.tiddev.authorization.config.SecurityConfig;
import com.tiddev.authorization.event.ReloadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
@RequestMapping("/cache")
@RequiredArgsConstructor
@RestController
public class CacheController {
    private static boolean LOG_STARTED = true;
    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;
    private final CacheProvider cacheProvider;
    private final AppConfig appConfig;

    @ResponseBody
    @GetMapping
    public void sendEventForCache() {
        ReloadEvent object = new ReloadEvent(appConfig.getSpring().getInstanceId());
        object.setClientId("test" + System.currentTimeMillis());
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", object);
    }

    @ResponseBody
    @GetMapping("/logger/start")
    public void start(@RequestParam(value = "seconds", defaultValue = "" + Integer.MAX_VALUE) int seconds) {
        startThroughputThread(seconds);
    }

    @ResponseBody
    @GetMapping("/logger/stop")
    public void stop() {
        LOG_STARTED = false;
    }

    public void startThroughputThread(int sec) {
        long start = System.currentTimeMillis();
        new Thread(() -> {
            long last = 0;
            while (LOG_STARTED) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    log.error("th ={} / s  , client={} , scopeList={} , scope={}", (SecurityConfig.REQUEST_RECEIVED.get() - last), cacheProvider.clientSize(),
                            cacheProvider.scopeListSize(), cacheProvider.scopeSize());
                    last = SecurityConfig.REQUEST_RECEIVED.get();
                    if (System.currentTimeMillis() - start > sec * 1000L)
                        LOG_STARTED = false;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }


    @ResponseBody
    @GetMapping("/info")
    public Map<String, Integer> show() {
        return Map.of("client", cacheProvider.clientSize(),
                "scopeList", cacheProvider.scopeListSize(),
                "scope", cacheProvider.scopeSize());
    }

    @ResponseBody
    @DeleteMapping
    public ResponseEntity<?> clear() {
        cacheProvider.invalidateAll();
        return ResponseEntity.noContent().build();
    }

}
