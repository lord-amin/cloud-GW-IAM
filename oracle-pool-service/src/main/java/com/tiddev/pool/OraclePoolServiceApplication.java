package com.tiddev.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;


@SpringBootApplication
@EnableDiscoveryClient
public class OraclePoolServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(OraclePoolServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OraclePoolServiceApplication.class, args);
        logConfig(context);
        logSwagger(context);

    }


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
    public static void logSwagger(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String host = environment.getProperty("JMX_HOST", "localhost");
        String port = environment.getProperty("server.port");
        String context = environment.getProperty("server.servlet.contextPath", "");
        StringBuilder base = new StringBuilder("http://").append(host).append(":").append(port).append(context);
        String json = base + environment.getProperty("springfox.documentation.swagger.v2.path", "/v2/api-docs");
        String ui = base + "/swagger-ui/index.html";
        LOGGER.warn("resource json address is [" + json + "]");
        LOGGER.warn("resource ui address is [" + ui + "]");
    }
}
