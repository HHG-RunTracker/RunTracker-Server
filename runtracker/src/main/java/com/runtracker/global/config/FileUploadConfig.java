package com.runtracker.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Getter
@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir:/app/uploads}}")
    private String uploadDir;

    @Value("${SPRING_DOMAIN}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            throw new IllegalStateException("Failed to create upload directory: " + uploadDir);
        }
    }
}