package com.tashiev.otpwithnikita.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final static String[] allowedMethods = {
            "GET", "POST", "PUT", "PATCH",
            "DELETE", "HEAD", "OPTIONS"
    };

    private final static String[] allowedHeaders = {
            "X-Requested-With", "Origin", "Content-Type",
            "Accept", "Authorization"
    };


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
//                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(1800);
    }
}