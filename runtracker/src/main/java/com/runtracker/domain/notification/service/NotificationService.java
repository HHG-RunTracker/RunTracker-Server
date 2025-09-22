package com.runtracker.domain.notification.service;

import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.member.service.MemberService;
import com.runtracker.global.fcm.FcmClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.runtracker.global.util.message.Messages;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ApplicationEventPublisher eventPublisher;
    private final FcmClient fcmClient;
    private final Messages messages;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final MemberService memberService;

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

        String title = messages.get("notify.crew.join.title");
        String content = messages.get("notify.crew.join.content", requestUser.getName());

        String fcmToken = memberService.getFcmToken(managerId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for manager - skipping join request notification: managerId={}", managerId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    @Transactional
    public void notifyCrewJoinRequestCancel(Long canceledUserId, Long managerId, Long crewId) {
        Member manager = memberRepository.findById(managerId).orElse(null);
        if (manager == null) {
            return;
        }

        Member canceledUser = memberRepository.findById(canceledUserId).orElse(null);
        if (canceledUser == null) {
            return;
        }

        String title = messages.get("notify.crew.cancel.title");
        String content = messages.get("notify.crew.cancel.content", canceledUser.getName());

        String fcmToken = memberService.getFcmToken(managerId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for manager - skipping cancel notification: managerId={}", managerId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    @Transactional
    public void notifyCrewJoinRequestApproval(Long approvedUserId, Long crewId, boolean isApproved) {
        Member approvedUser = memberRepository.findById(approvedUserId).orElse(null);
        if (approvedUser == null) {
            return;
        }

        Crew crew = crewRepository.findById(crewId).orElse(null);
        if (crew == null) {
            return;
        }

        String title = isApproved ?
            messages.get("notify.crew.approved.title") :
            messages.get("notify.crew.rejected.title");
        String content = isApproved ?
            messages.get("notify.crew.approved.content", crew.getTitle()) :
            messages.get("notify.crew.rejected.content", crew.getTitle());

        String fcmToken = memberService.getFcmToken(approvedUserId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for approved user - skipping approval notification: approvedUserId={}", approvedUserId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }
}