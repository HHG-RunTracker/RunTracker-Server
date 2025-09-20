package com.runtracker.domain.notification.event;

import com.runtracker.global.fcm.FcmClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {

    private final FcmClient fcmClient;

    @EventListener
    @Async
    public void handleNotificationEvent() {

    }
}