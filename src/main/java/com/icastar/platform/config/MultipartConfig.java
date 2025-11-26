package com.icastar.platform.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set maximum file size (50MB)
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        
        // Set maximum request size (50MB)
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        
        // Set file size threshold (2KB)
        factory.setFileSizeThreshold(DataSize.ofKilobytes(2));
        
        return factory.createMultipartConfig();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
