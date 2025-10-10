package com.runtracker.domain.upload.controller;

import com.runtracker.domain.upload.service.FileStorageService;
import com.runtracker.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        String fileUrl = fileStorageService.uploadImage(file);

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);

        return ApiResponse.ok(response);
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {

        Resource resource = fileStorageService.loadFileAsResource(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/webp"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}