package com.runtracker.domain.upload.service;

import com.runtracker.domain.upload.exception.*;
import com.runtracker.global.util.ImageConverter;
import com.runtracker.global.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileUploadConfig fileUploadConfig;

    // 이미지 파일을 업로드하고 URL을 반환
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileIsEmptyException();
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException();
        }

        return storeFile(file);
    }

    // 파일을 WebP 포맷으로 변환
    private String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFilename.contains("..")) {
            throw new InvalidFileNameException();
        }

        try {
            String storedFilename = UUID.randomUUID() + ".webp";

            Path targetLocation = Paths.get(fileUploadConfig.getUploadDir()).resolve(storedFilename);

            InputStream webpInputStream = ImageConverter.convertToWebP(file);

            Files.copy(webpInputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileUploadConfig.getBaseUrl() + "/api/upload/image/" + storedFilename;

        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFilename, ex);
            throw new FileStorageFailedException(originalFilename);
        }
    }

    // Base64 인코딩된 이미지를 파일로 저장하고 URL을 반환
    public String uploadBase64Image(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            return null;
        }

        try {
            String base64Image = base64Data;
            if (base64Data.contains(",")) {
                base64Image = base64Data.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            String storedFilename = UUID.randomUUID() + ".webp";
            Path targetLocation = Paths.get(fileUploadConfig.getUploadDir()).resolve(storedFilename);

            InputStream imageInputStream = new ByteArrayInputStream(imageBytes);
            InputStream webpInputStream = ImageConverter.convertToWebP(imageInputStream);

            Files.copy(webpInputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileUploadConfig.getBaseUrl() + "/api/upload/image/" + storedFilename;

        } catch (Exception ex) {
            log.error("Failed to store base64 image", ex);
            throw new FileStorageFailedException("base64-image");
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = Paths.get(fileUploadConfig.getUploadDir()).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException(filename);
            }
        } catch (MalformedURLException ex) {
            log.error("Failed to load file: {}", filename, ex);
            throw new FileNotFoundException(filename);
        }
    }
}