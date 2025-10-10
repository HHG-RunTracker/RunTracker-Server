package com.runtracker.global.util;

import com.runtracker.domain.upload.exception.ImageConversionFailedException;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


@Slf4j
public class ImageConverter {

    private static final int DEFAULT_QUALITY = 80;
    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1920;

    // MultipartFile을 WebP 포맷의 InputStream으로 변환
    public static InputStream convertToWebP(MultipartFile file) {
        return convertToWebP(file, DEFAULT_QUALITY);
    }

    // MultipartFile을 WebP 포맷의 InputStream으로 변환
    public static InputStream convertToWebP(MultipartFile file, int quality) {
        try {
            ImmutableImage image = ImmutableImage.loader().fromBytes(file.getBytes());

            if (image.width > MAX_WIDTH || image.height > MAX_HEIGHT) {
                double scale = Math.min((double) MAX_WIDTH / image.width, (double) MAX_HEIGHT / image.height);
                int newWidth = (int) (image.width * scale);
                int newHeight = (int) (image.height * scale);
                image = image.scaleTo(newWidth, newHeight);
                log.info("Image resized from {}x{} to {}x{}",
                    image.width, image.height, newWidth, newHeight);
            }

            WebpWriter writer = WebpWriter.DEFAULT.withQ(quality);
            byte[] webpBytes = image.bytes(writer);

            return new ByteArrayInputStream(webpBytes);

        } catch (Exception e) {
            log.error("Failed to convert image to WebP: {}", file.getOriginalFilename(), e);
            throw new ImageConversionFailedException(file.getOriginalFilename());
        }
    }
}