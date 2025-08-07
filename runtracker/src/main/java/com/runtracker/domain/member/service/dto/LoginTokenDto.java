package com.runtracker.domain.member.service.dto;

import com.runtracker.global.jwt.dto.TokenDataDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTokenDto {
    private Long userId;
    private String socialId;
    private TokenDataDto tokenData;
    
    @Getter
    public static class SocialIdLoginRequest {
        @NotBlank(message = "소셜 ID는 필수입니다")
        private String socialId;
        
        @NotBlank(message = "인증 키는 필수입니다")
        private String key;
    }
    
    @Getter
    @Builder
    public static class MemberSearchResult {
        private Long userId;
        private String socialId;
    }
    
    @Getter
    public static class RefreshTokenRequest {
        @NotBlank(message = "리프레시 토큰은 필수입니다")
        private String refreshToken;
    }
}