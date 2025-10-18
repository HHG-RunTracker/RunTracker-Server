package com.runtracker.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Getter
@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir:/app/uploads}")
    private String uploadDir;

    @Value("${app.domain:http://localhost:8080}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                System.out.println("Warning: Failed to create upload directory: " + uploadDir);
            }
        }
    }
}