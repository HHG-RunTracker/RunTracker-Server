package com.runtracker.domain.notification.eventHandler;

import com.runtracker.domain.crew.event.CrewJoinRequestEvent;
import com.runtracker.domain.crew.event.CrewJoinRequestCancelEvent;
import com.runtracker.domain.crew.event.CrewJoinRequestApprovalEvent;
import com.runtracker.domain.crew.event.CrewMemberRoleUpdateEvent;
import com.runtracker.domain.crew.event.CrewDeleteEvent;
import com.runtracker.domain.crew.event.CrewBanEvent;
import com.runtracker.domain.crew.event.CrewLeaveEvent;
import com.runtracker.domain.community.event.PostCreateEvent;
import com.runtracker.domain.community.event.PostUpdateEvent;
import com.runtracker.domain.community.event.PostDeleteEvent;
import com.runtracker.domain.community.event.PostLikeEvent;
import com.runtracker.domain.community.event.PostCommentEvent;
import com.runtracker.domain.schedule.event.ScheduleCreateEvent;
import com.runtracker.domain.schedule.event.ScheduleUpdateEvent;
import com.runtracker.domain.schedule.event.ScheduleDeleteEvent;
import com.runtracker.domain.schedule.event.ScheduleJoinEvent;
import com.runtracker.domain.schedule.event.ScheduleCancelEvent;
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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewDeleteNotification(CrewDeleteEvent event) {
        try {
            for (Long memberId : event.memberIds()) {
                notificationService.notifyCrewDeletion(memberId, event.crewTitle());
            }
        } catch (Exception e) {
            log.error("Failed to send crew delete notification - memberIds: {}, crewTitle: {}, error: {}",
                event.memberIds(),
                event.crewTitle(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewBanNotification(CrewBanEvent event) {
        try {
            notificationService.notifyCrewBan(event.bannedMemberId(), event.crewTitle());
        } catch (Exception e) {
            log.error("Failed to send crew ban notification - bannedMemberId: {}, crewTitle: {}, error: {}",
                event.bannedMemberId(),
                event.crewTitle(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCrewLeaveNotification(CrewLeaveEvent event) {
        try {
            for (Long managerId : event.managerIds()) {
                notificationService.notifyCrewLeave(managerId, event.leavingMemberName());
            }
        } catch (Exception e) {
            log.error("Failed to send crew leave notification - managerIds: {}, leavingMemberName: {}, error: {}",
                event.managerIds(),
                event.leavingMemberName(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPostLikeNotification(PostLikeEvent event) {
        try {
            notificationService.notifyPostLike(event.likerMemberId(), event.postAuthorMemberId());
        } catch (Exception e) {
            log.error("Failed to send post like notification - likerMemberId: {}, postAuthorMemberId: {}, postId: {}, error: {}",
                event.likerMemberId(),
                event.postAuthorMemberId(),
                event.postId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPostCommentNotification(PostCommentEvent event) {
        try {
            notificationService.notifyPostComment(event.commenterMemberId(), event.postAuthorMemberId());
        } catch (Exception e) {
            log.error("Failed to send post comment notification - commenterMemberId: {}, postAuthorMemberId: {}, postId: {}, error: {}",
                event.commenterMemberId(),
                event.postAuthorMemberId(),
                event.postId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPostCreateNotification(PostCreateEvent event) {
        try {
            notificationService.notifyPostCreate(event.authorMemberId());
        } catch (Exception e) {
            log.error("Failed to send post create notification - authorMemberId: {}, postId: {}, error: {}",
                event.authorMemberId(),
                event.postId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPostUpdateNotification(PostUpdateEvent event) {
        try {
            notificationService.notifyPostUpdate(event.authorMemberId());
        } catch (Exception e) {
            log.error("Failed to send post update notification - authorMemberId: {}, postId: {}, error: {}",
                event.authorMemberId(),
                event.postId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPostDeleteNotification(PostDeleteEvent event) {
        try {
            notificationService.notifyPostDelete(event.authorMemberId());
        } catch (Exception e) {
            log.error("Failed to send post delete notification - authorMemberId: {}, postId: {}, error: {}",
                event.authorMemberId(),
                event.postId(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendScheduleCreateNotification(ScheduleCreateEvent event) {
        try {
            notificationService.notifyScheduleCreation(event.creatorId(), event.crewId(), event.scheduleTitle());
        } catch (Exception e) {
            log.error("Failed to send schedule creation notification - creatorId: {}, crewId: {}, scheduleTitle: {}, error: {}",
                event.creatorId(),
                event.crewId(),
                event.scheduleTitle(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendScheduleUpdateNotification(ScheduleUpdateEvent event) {
        try {
            notificationService.notifyScheduleUpdate(event.updaterId(), event.crewId(), event.scheduleTitle());
        } catch (Exception e) {
            log.error("Failed to send schedule update notification - updaterId: {}, crewId: {}, scheduleTitle: {}, error: {}",
                event.updaterId(),
                event.crewId(),
                event.scheduleTitle(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendScheduleDeleteNotification(ScheduleDeleteEvent event) {
        try {
            notificationService.notifyScheduleDelete(event.deleterId(), event.crewId(), event.scheduleTitle());
        } catch (Exception e) {
            log.error("Failed to send schedule delete notification - deleterId: {}, crewId: {}, scheduleTitle: {}, error: {}",
                event.deleterId(),
                event.crewId(),
                event.scheduleTitle(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendScheduleJoinNotification(ScheduleJoinEvent event) {
        try {
            notificationService.notifyScheduleJoin(event.participantId(), event.crewId(), event.scheduleTitle());
        } catch (Exception e) {
            log.error("Failed to send schedule join notification - participantId: {}, crewId: {}, scheduleTitle: {}, error: {}",
                event.participantId(),
                event.crewId(),
                event.scheduleTitle(),
                e.getMessage());
            throw e;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendScheduleCancelNotification(ScheduleCancelEvent event) {
        try {
            notificationService.notifyScheduleCancel(event.participantId(), event.crewId(), event.scheduleTitle());
        } catch (Exception e) {
            log.error("Failed to send schedule cancel notification - participantId: {}, crewId: {}, scheduleTitle: {}, error: {}",
                event.participantId(),
                event.crewId(),
                event.scheduleTitle(),
                e.getMessage());
            throw e;
        }
    }
}