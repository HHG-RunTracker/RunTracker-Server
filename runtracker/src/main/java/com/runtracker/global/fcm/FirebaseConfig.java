package com.runtracker.global.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-key:#{null}}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials googleCredentials;
            String firebaseJson = System.getenv("FCM_JSON");

            if (firebaseJson != null && !firebaseJson.isEmpty()) {
                googleCredentials = GoogleCredentials.fromStream(
                    new java.io.ByteArrayInputStream(firebaseJson.getBytes())
                );
            } else if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
                String resourcePath = serviceAccountKeyPath.startsWith("classpath:")
                    ? serviceAccountKeyPath.substring(10)
                    : serviceAccountKeyPath;
                googleCredentials = GoogleCredentials
                        .fromStream(new ClassPathResource(resourcePath).getInputStream());
            } else {
                throw new RuntimeException("Firebase service account key not found. Please set FCM_JSON or FIREBASE_SERVICE_ACCOUNT_KEY");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}