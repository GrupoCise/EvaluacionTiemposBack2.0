package com.web.back.config;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShutdownConfig {

    @Bean
    public Runnable shutdownHook(ConfigurableApplicationContext context) {
        return () -> Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (context.isActive()) {
                context.close();
            }
        }));
    }
}
