package com.lhsdev.cmsproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lhsdev.cmsproject.interceptor.GeoBlockingInterceptor;
import com.lhsdev.cmsproject.interceptor.VisitorInterceptor;

import lombok.RequiredArgsConstructor;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final VisitorInterceptor visitorInterceptor;
    private final GeoBlockingInterceptor geoBlockingInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure the directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println("Created upload directory: " + directory.getAbsolutePath() + " -> " + created);
        }

        // Map URL path '/uploads/**' to physical directory
        String path = uploadDir;
        if (!path.endsWith("/") && !path.endsWith("\\")) {
            path += "/";
        }

        // Add file protocol prefix
        if (!path.startsWith("file:")) {
            path = "file:///" + path;
        }

        System.out.println("Mapping /uploads/** to " + path);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Geo Blocking First
        registry.addInterceptor(geoBlockingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**", "/js/**", "/images/**", "/uploads/**",
                        "/h2-console/**", "/error", "/favicon.ico");

        registry.addInterceptor(visitorInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**", "/js/**", "/images/**", "/uploads/**",
                        "/h2-console/**", "/admin/**", "/api/**", "/error",
                        "/favicon.ico");
    }

}
