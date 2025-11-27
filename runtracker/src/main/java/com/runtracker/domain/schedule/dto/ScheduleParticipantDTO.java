package com.runtracker.domain.schedule.dto;

import com.runtracker.domain.member.entity.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ScheduleParticipantDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long memberId;
        private String memberName;
        private MemberRole role;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private List<Response> participants;
        private int participantCount;

        public static ListResponse of(List<Response> participants) {
            return ListResponse.builder()
                    .participants(participants)
                    .participantCount(participants.size())
                    .build();
        }
    }
}