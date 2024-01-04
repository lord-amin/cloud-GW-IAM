package com.tiddev.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.File;

@RefreshScope
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
public class ConfigServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ConfigServiceApplication.class, args);
        logConfig(context);
        ConfigurableEnvironment environment = context.getEnvironment();
        {
            String base = "http://" + environment.getProperty("server.address", "localhost")
                    + ":" + environment.getProperty("server.port") + environment.getProperty("server.servlet.contextPath", "") + environment.getProperty("spring.cloud.config.server.prefix", "/");
            String property = environment.getProperty("spring.cloud.config.server.native.search-locations");
            if (!environment.getProperty("spring.profiles.active", "").equals("native"))
                return;
            String[] dirs = property.split(",");
            for (String dir : dirs) {
                File d = new File(dir.replaceFirst("file:", ""));
                if (!d.exists()) {
                    LOGGER.error("The config dir {} not found", dir);
                    continue;
                }
                File[] files = d.listFiles();

                for (File file : files) {
                    LOGGER.info(base + "/" + file.getName());
                }
            }
        }
        {
            SpringDocConfigProperties swagger = context.getBean(SpringDocConfigProperties.class);
            String property = environment.getProperty("server.address");
            String property1 = environment.getProperty("server.servlet.contextPath");
            String base = "http://" + (property == null ? "localhost" : property) + ":" + environment.getProperty("server.port") + (property1 == null ? "" : property1);
            String json = base + environment.getProperty("springdoc.api-docs.path", swagger.getApiDocs().getPath());
            String ui = base + "/swagger-ui.html";
            LOGGER.info("resource json address is [" + json + "]");
            LOGGER.info("resource ui address is [" + ui + "]");
        }
    }

//    @Bean("restTemplate")
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    private static void logConfig(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof MapPropertySource propertySource1) {
                for (String propertyName : propertySource1.getPropertyNames()) {
                    LOGGER.info("       {}={}", propertyName, environment.getProperty(propertyName));
                }
            }
        }
    }
}
