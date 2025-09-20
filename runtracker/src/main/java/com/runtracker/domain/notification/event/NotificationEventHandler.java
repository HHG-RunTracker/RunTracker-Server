package com.runtracker.domain.notification.event;

import com.runtracker.domain.crew.event.CrewJoinRequestEvent;
import com.runtracker.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewJoinRequestNotification(CrewJoinRequestEvent event) {
        try {
            notificationService.notifyCrewJoinRequest(event.requestUserId(), event.managerId(), event.crewId());
        } catch (Exception e) {
            log.error("크루 가입 요청 알림 전송 실패 - 요청자ID: {}, 매니저ID: {}, 크루ID: {}, 에러: {}",
                event.requestUserId(),
                event.managerId(),
                event.crewId(),
                e.getMessage());
            throw e;
        }
    }
}