package com.tiddev.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yaser(amin) Sadeghi
 */
@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {
    @Setter
    @Getter
    private Security security;

    public static class Security {
        @Setter
        @Getter
        private String[] ignorePathList;
    }
}
