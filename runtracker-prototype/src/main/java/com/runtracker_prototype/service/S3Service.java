package com.runtracker_prototype.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.net.URL;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class S3Service {
//    private final S3Presigner s3Presigner;
//
//    @Value("${cloud.aws.s3.bucket-name}")
//    private String bucketName;
//
//    /**
//     * PreSigned URL을 생성
//     * @param objectKey 업로드할 객체(파일명)
//     * @return PreSigned URL
//     */
//    public URL generatePresignedUrl(String objectKey) {
//        String contentType = "image/jpeg";
//        if (objectKey.endsWith(".png")) {
//            contentType = "image/png";
//        }
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(objectKey)
//                .contentType(contentType)
//                .build();
//
//        PresignedPutObjectRequest preSignedRequest = s3Presigner.presignPutObject(p -> p
//                .signatureDuration(Duration.ofMinutes(10))  // URL 유효 시간
//                .putObjectRequest(putObjectRequest)
//        );
//
//        return preSignedRequest.url();
//    }
}
