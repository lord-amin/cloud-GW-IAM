package com.tiddev.authorization;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AuthenticationServiceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AuthenticationServiceApplication.class, args);
        logConfig(context);
        swagger(context);
        try {
            checkRedis(context);
        } catch (Exception e) {
            log.info("======================= cache provider =============================");
            log.info("                        heap ");
            log.info("==================================================================");
        }
        try {
            checkRabbit(context);
        } catch (Exception e) {
            log.info("======================= cache provider =============================");
            log.info("                        REDIS ");
            log.info("==================================================================");
        }
    }

    private static void checkRedis(ConfigurableApplicationContext context) {
        context.getBean(RedissonClient.class);
    }

    private static void checkRabbit(ConfigurableApplicationContext context) {
        context.getBean(RabbitTemplate.class);
    }

    private static void logConfig(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof MapPropertySource propertySource1) {
                for (String propertyName : propertySource1.getPropertyNames()) {
                    log.warn("       {}={}", propertyName, environment.getProperty(propertyName));
                }
            }
        }
    }

    private static void swagger(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String property = environment.getProperty("server.address");
        String property1 = environment.getProperty("server.servlet.contextPath");
        String base = "http://" + (property == null ? "localhost" : property) + ":" + environment.getProperty("server.port") + (property1 == null ? "" : property1);
        String json = base + environment.getProperty("springdoc.api-docs.path", "/v3/api-docs");
        String ui = base + "/swagger-ui.html";
        log.warn("resource json address is [" + json + "]");
        log.warn("resource ui address is [" + ui + "]");
    }

}
