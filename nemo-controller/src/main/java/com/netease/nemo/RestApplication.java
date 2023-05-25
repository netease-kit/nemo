package com.netease.nemo;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.netease.nemo.util.gson.GsonBox;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@MapperScan(basePackages = "com.netease.nemo.mapper")
@SpringBootApplication
@EnableAutoConfiguration
@EnableWebMvc
@EnableScheduling
@Configuration
@ComponentScan("com.netease.nemo")
public class RestApplication {

    @Bean
    public Gson gson() {
        return GsonBox.OPEN.gson();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(Lists.newArrayList("POST", "GET", "PUT", "OPTIONS", "DELETE")); //允许的HTTP请求方法
        corsConfiguration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class);
    }
}