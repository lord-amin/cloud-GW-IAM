package com.tiddev.authorization.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties
@Configuration
public class AppConfig {

    @Getter
    private Spring spring;

    @Autowired
    public void setSpring(Spring spring) {
        this.spring = spring;
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "spring.application")
    @Component
    public static class Spring {
        private String instanceId;
        private String name;
    }
}
