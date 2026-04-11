package com.cambofreelance.websiteservice.logger.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ProjectConfig {

    private static Environment environment;

    @Autowired
    public ProjectConfig(Environment environment) {
        ProjectConfig.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static <T> T getProperty(String key, Class<T> type) {
        return environment.getProperty(key, type);
    }
}
