package com.runtracker.global.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenWithPhoneNumber {
        private String phoneNumber;
        private String fcmToken;
    }

    private List<TokenWithPhoneNumber> tokenPairs;

    public static TokenWithPhoneNumber of(String phoneNumber, String fcmToken) {
        return TokenWithPhoneNumber.builder()
                .phoneNumber(phoneNumber)
                .fcmToken(fcmToken)
                .build();
    }

    public static FcmTokenDto of(String phoneNumber, List<String> fcmTokens) {
        if (fcmTokens == null || fcmTokens.isEmpty()) {
            return FcmTokenDto.builder().tokenPairs(List.of()).build();
        }

        List<TokenWithPhoneNumber> tokenPairs = fcmTokens.stream()
                .map(token -> of(phoneNumber, token))
                .toList();

        return FcmTokenDto.builder()
                .tokenPairs(tokenPairs)
                .build();
    }
}