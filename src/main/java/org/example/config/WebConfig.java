package org.example.config;

import org.example.service.upload.FileUpLoadService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final FileUpLoadService uploads;

    public WebConfig(FileUpLoadService uploads) {
        this.uploads = uploads;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploads.root().toUri().toString().replaceAll("/*$", "/");
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}