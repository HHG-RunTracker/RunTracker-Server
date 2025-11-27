package com.runtracker.domain.auth.eventHandler;

import com.runtracker.domain.auth.eventHandler.dto.KakaoOAuth2UserInfo;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.service.MemberService;
import com.runtracker.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2EventHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        log.info("OAuth2 Success - attributes: {}", oAuth2User.getAttributes());

        KakaoOAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

        Member member = memberService.createOrUpdateMember(
                "kakao", 
                userInfo.getSocialId(), 
                userInfo.getProfileImage(), 
                userInfo.getNickname()
        );

        String accessToken = jwtUtil.generateAccessToken(member.getId(), member.getSocialId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getId());

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}