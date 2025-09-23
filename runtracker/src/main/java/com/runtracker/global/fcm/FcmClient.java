package com.runtracker.global.fcm;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmClient {

    public Boolean send(String title, String body, String token) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().sendAsync(message).get();

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.warn("Failed to send FCM message. token: {}", token, e);
            return false;
        }
    }
}