package com.rzh12.notevino.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://notevino.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持 OPTIONS 方法
                .allowedHeaders("*")
                .allowCredentials(true) // 根據需求允許憑證
                .maxAge(3600); // 增加 maxAge 減少預檢請求
    }
}
