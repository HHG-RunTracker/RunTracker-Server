package com.runtracker.domain.crew.dto;

import lombok.*;

public class CrewApprovalDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long memberId;     // 신청한 회원 ID
        private Boolean approved;  // 승인 유무 (true: 승인, false: 거절)
    }
}