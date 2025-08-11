package com.runtracker.domain.member.service;

import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public Member createOrUpdateMember(String socialAttr, String socialId, 
                                      String photo, String name) {
        Optional<Member> existingMember = memberRepository.findBySocialId(socialId);
        
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            if (photo != null) {
                member.updatePhoto(photo);
            }
            return member;
        }
        
        Member newMember = Member.builder()
                .socialAttr(socialAttr)
                .socialId(socialId)
                .photo(photo)
                .name(name)
                .build();
        
        return memberRepository.save(newMember);
    }


    public Member getMemberByName(String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Member not found with name: " + name));
    }

    public Member getMemberBySocialId(String socialId) {
        return memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new RuntimeException("Member not found with socialId: " + socialId));
    }

    @Transactional(readOnly = true)
    public LoginTokenDto.MemberSearchResult findMemberByName(String name) {
        Member member = getMemberByName(name);
        
        log.info("find member by name - userId: {}, socialId: {}", member.getId(), member.getSocialId());
        
        return LoginTokenDto.MemberSearchResult.builder()
                .userId(member.getId())
                .socialId(member.getSocialId())
                .build();
    }

    @Transactional
    public void logout(Long memberId) {
        // 현재 요청에서 토큰 추출
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    jwtUtil.blacklistToken(token);
                } catch (Exception e) {
                    log.error("Failed to blacklist token for user: {}", memberId, e);
                }
            }
        }
    }
}