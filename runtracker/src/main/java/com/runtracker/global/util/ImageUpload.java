package com.runtracker.global.util;

import com.runtracker.domain.upload.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUpload {

    private final FileStorageService fileStorageService;

    // Base64 이미지 데이터를 URL로 변환
    public String convertBase64ToUrlIfNeeded(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }

        try {
            // Base64 데이터인지 확인
            if (data.startsWith("data:image") || data.length() > 500) {
                return fileStorageService.uploadBase64Image(data);
            }
            // 이미 URL인 경우 그대로 반환
            return data;
        } catch (Exception e) {
            log.error("Failed to convert Base64 image to URL", e);
            return data;
        }
    }
}