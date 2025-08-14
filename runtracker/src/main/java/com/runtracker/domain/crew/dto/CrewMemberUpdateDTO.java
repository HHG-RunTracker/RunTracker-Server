package com.runtracker.domain.crew.dto;

import com.runtracker.domain.member.entity.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CrewMemberUpdateDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long memberId;
        private MemberRole role;
    }
}