package com.lhsdev.cmsproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path '/uploads/**' to physical directory
        String path = "file:" + uploadDir;

        // Ensure the directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
    }
}
