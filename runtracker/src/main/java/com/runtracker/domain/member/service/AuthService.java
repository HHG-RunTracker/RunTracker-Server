package com.runtracker.domain.member.service;

import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.global.jwt.JwtUtil;
import com.runtracker.global.jwt.dto.TokenDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    
    @Value("${app.auth.key}")
    private String authKey;

    @Transactional
    public TokenDataDto refreshToken(String refreshToken) {
        return jwtUtil.refreshToken(refreshToken);
    }

    @Transactional
    public LoginTokenDto testLoginBySocialId(String socialId, String key) {
        validateAuthKey(key);
        
        Member member = memberService.getMemberBySocialId(socialId);

        log.info("test login by socialId - userId: {}", member.getId());

        TokenDataDto tokenData = jwtUtil.createTokenData(member.getId(), member.getSocialId());
        
        return LoginTokenDto.builder()
                .userId(member.getId())
                .socialId(member.getSocialId())
                .tokenData(tokenData)
                .build();
    }
    
    private void validateAuthKey(String key) {
        if (!authKey.equals(key)) {
            throw new RuntimeException("Invalid authentication key");
        }
    }
}