package com.runtracker.domain.upload.service;

import com.runtracker.domain.upload.exception.*;
import com.runtracker.global.util.ImageConverter;
import com.runtracker.global.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;
    private final S3Config s3Config;

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

    // 파일을 WebP 포맷으로 변환하여 S3에 업로드
    private String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFilename.contains("..")) {
            throw new InvalidFileNameException();
        }

        try {
            String storedFilename = UUID.randomUUID() + ".webp";

            InputStream webpInputStream = ImageConverter.convertToWebP(file);
            byte[] fileBytes = webpInputStream.readAllBytes();

            uploadToS3(storedFilename, fileBytes);

            return s3Config.getBaseUrl() + "/" + storedFilename;

        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFilename, ex);
            throw new FileStorageFailedException(originalFilename);
        }
    }

    private void uploadToS3(String filename, byte[] fileBytes) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(filename)
                    .contentType("image/webp")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
            log.info("File uploaded to S3: {}", filename);
        } catch (Exception ex) {
            log.error("Failed to upload file to S3: {}", filename, ex);
            throw new FileStorageFailedException(filename);
        }
    }

    // Base64 인코딩된 이미지를 S3에 업로드하고 URL을 반환
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

            InputStream imageInputStream = new ByteArrayInputStream(imageBytes);
            InputStream webpInputStream = ImageConverter.convertToWebP(imageInputStream);
            byte[] webpBytes = webpInputStream.readAllBytes();

            uploadToS3(storedFilename, webpBytes);

            return s3Config.getBaseUrl() + "/" + storedFilename;

        } catch (Exception ex) {
            log.error("Failed to store base64 image", ex);
            throw new FileStorageFailedException("base64-image");
        }
    }

    public String getImageUrl(String filename) {
        return s3Config.getBaseUrl() + "/" + filename;
    }
}