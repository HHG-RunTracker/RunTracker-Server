package com.runtracker.domain.notification.eventHandler;

import com.runtracker.domain.crew.event.CrewJoinRequestEvent;
import com.runtracker.domain.crew.event.CrewJoinRequestCancelEvent;
import com.runtracker.domain.crew.event.CrewJoinRequestApprovalEvent;
import com.runtracker.domain.crew.event.CrewMemberRoleUpdateEvent;
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
            log.error("Failed to send crew join request notification - requestUserId: {}, managerId: {}, crewId: {}, error: {}",
                event.requestUserId(),
                event.managerId(),
                event.crewId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewJoinRequestCancelNotification(CrewJoinRequestCancelEvent event) {
        try {
            notificationService.notifyCrewJoinRequestCancel(event.canceledUserId(), event.managerId(), event.crewId());
        } catch (Exception e) {
            log.error("Failed to send crew join request cancel notification - canceledUserId: {}, managerId: {}, crewId: {}, error: {}",
                event.canceledUserId(),
                event.managerId(),
                event.crewId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewJoinRequestApprovalNotification(CrewJoinRequestApprovalEvent event) {
        try {
            notificationService.notifyCrewJoinRequestApproval(event.approvedUserId(), event.crewId(), event.isApproved());
        } catch (Exception e) {
            log.error("Failed to send crew join request approval notification - approvedUserId: {}, crewId: {}, isApproved: {}, error: {}",
                event.approvedUserId(),
                event.crewId(),
                event.isApproved(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewMemberRoleUpdateNotification(CrewMemberRoleUpdateEvent event) {
        try {
            notificationService.notifyCrewMemberRoleUpdate(event.targetMemberId(), event.crewId(), event.newRole());
        } catch (Exception e) {
            log.error("Failed to send crew member role update notification - targetMemberId: {}, crewId: {}, newRole: {}, error: {}",
                event.targetMemberId(),
                event.crewId(),
                event.newRole(),
                e.getMessage());
            throw e;
        }
    }
}