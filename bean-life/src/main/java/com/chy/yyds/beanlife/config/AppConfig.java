package com.chy.yyds.beanlife.config;

import com.chy.yyds.beanlife.service.TestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean(initMethod = "init")
    public TestService myBean() {
        return new TestService();
    }
}
