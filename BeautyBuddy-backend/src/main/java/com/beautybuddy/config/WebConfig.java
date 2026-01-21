package com.beautybuddy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")                   // all API paths
                .allowedOrigins("http://localhost:5173") // your frontend domain
                .allowedMethods("GET", "POST", "PUT", "DELETE") // allowed HTTP methods
                .allowedHeaders("*")                // allow all headers
                .allowCredentials(true)             // allow cookies/auth
                .maxAge(3600);                      // cache preflight response for 1 hour
    }
}
