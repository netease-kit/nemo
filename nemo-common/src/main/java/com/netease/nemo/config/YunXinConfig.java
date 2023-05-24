package com.netease.nemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YunXinConfig {

    @Bean
    @ConfigurationProperties(prefix = "yunxin.origin")
    public YunXinConfigProperties yunXinConfigProperties() {
        return new YunXinConfigProperties();
    }
}
