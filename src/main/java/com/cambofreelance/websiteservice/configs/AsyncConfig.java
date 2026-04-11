package com.cambofreelance.websiteservice.configs;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// AsyncConfig.java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "depositTaskExecutor")
    public Executor depositTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);      // always-alive threads
        executor.setMaxPoolSize(20);      // burst capacity
        executor.setQueueCapacity(100);   // backlog before rejection
        executor.setThreadNamePrefix("deposit-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30); // graceful shutdown
        executor.initialize();
        return executor;
    }
}