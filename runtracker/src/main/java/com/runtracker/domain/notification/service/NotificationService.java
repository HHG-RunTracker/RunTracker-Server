package com.runtracker.domain.notification.service;

import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.member.entity.enums.MemberRole;
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

    @Transactional
    public void notifyCrewMemberRoleUpdate(Long targetMemberId, Long crewId, MemberRole newRole) {
        Crew crew = crewRepository.findById(crewId).orElse(null);
        if (crew == null) {
            return;
        }

        String roleDisplayName = getRoleDisplayName(newRole);
        String title = messages.get("notify.crew.role.update.title");
        String content = messages.get("notify.crew.role.update.content", crew.getTitle(), roleDisplayName);

        String fcmToken = memberService.getFcmToken(targetMemberId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for target member - skipping role update notification: targetMemberId={}", targetMemberId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    @Transactional
    public void notifyCrewDeletion(Long memberId, String crewTitle) {
        String title = messages.get("notify.crew.delete.title");
        String content = messages.get("notify.crew.delete.content", crewTitle);

        String fcmToken = memberService.getFcmToken(memberId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for member - skipping crew deletion notification: memberId={}", memberId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    @Transactional
    public void notifyCrewBan(Long memberId, String crewTitle) {
        String title = messages.get("notify.crew.ban.title");
        String content = messages.get("notify.crew.ban.content", crewTitle);

        String fcmToken = memberService.getFcmToken(memberId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for member - skipping crew ban notification: memberId={}", memberId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    @Transactional
    public void notifyCrewLeave(Long managerId, String leavingMemberName) {
        String title = messages.get("notify.crew.leave.title");
        String content = messages.get("notify.crew.leave.content", leavingMemberName);

        String fcmToken = memberService.getFcmToken(managerId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for manager - skipping crew leave notification: managerId={}", managerId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    @Transactional
    public void notifyPostLike(Long likerMemberId, Long postAuthorMemberId) {
        if (likerMemberId.equals(postAuthorMemberId)) {
            return;
        }

        Member likerMember = memberRepository.findById(likerMemberId).orElse(null);
        if (likerMember == null) {
            return;
        }

        String title = messages.get("notify.post.like.title");
        String content = messages.get("notify.post.like.content", likerMember.getName());

        String fcmToken = memberService.getFcmToken(postAuthorMemberId).orElse(null);
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token not found for post author - skipping post like notification: postAuthorMemberId={}", postAuthorMemberId);
            return;
        }

        fcmClient.send(title, content, fcmToken);
    }

    private String getRoleDisplayName(MemberRole role) {
        return switch (role) {
            case CREW_LEADER -> "크루장";
            case CREW_MANAGER -> "크루 매니저";
            case CREW_MEMBER -> "크루 멤버";
            default -> "일반 유저";
        };
    }
}