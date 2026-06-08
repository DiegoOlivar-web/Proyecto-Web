package com.example.Proyecto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from classpath:/static/img/ (default) and also from the workspace img/ folder
        registry
            .addResourceHandler("/img/**")
            .addResourceLocations("classpath:/static/img/", "file:../img/");
    }
}
