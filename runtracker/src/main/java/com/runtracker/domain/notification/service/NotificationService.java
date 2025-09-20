package com.runtracker.domain.notification.service;

import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.global.fcm.FcmClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.runtracker.global.util.message.Messages;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ApplicationEventPublisher eventPublisher;
    private final FcmClient fcmClient;
    private final Messages messages;
    private final MemberRepository memberRepository;

    @Transactional
    public void notifyCrewJoinRequest(Long requestUserId, Long managerId, Long crewId) {
        Member manager = memberRepository.findById(managerId).orElse(null);
        if (manager == null) {
            return;
        }

        Member requestUser = memberRepository.findById(requestUserId).orElse(null);
        if (requestUser == null) {
            return;
        }

        String title = messages.get("notify.crew.join.Request.title");
        String content = messages.get("notify.crew.join.Request.content", requestUser.getName());

        if (manager.getFcmToken() == null || manager.getFcmToken().trim().isEmpty()) {
            log.info("FCM token not found for manager - skipping notification: managerId={}", managerId);
            return;
        }

        boolean isSendSuccess = fcmClient.send(title, content, manager.getFcmToken());
    }
}