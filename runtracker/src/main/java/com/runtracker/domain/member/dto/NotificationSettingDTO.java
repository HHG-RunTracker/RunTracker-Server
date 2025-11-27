package com.runtracker.domain.member.dto;

import lombok.*;

public class NotificationSettingDTO {
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Boolean notifyBlock;
    }
}