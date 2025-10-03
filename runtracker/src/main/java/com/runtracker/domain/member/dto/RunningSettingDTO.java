package com.runtracker.domain.member.dto;

import com.runtracker.domain.member.entity.Member;
import lombok.*;

public class RunningSettingDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Double dailyDistanceGoal;
        private Integer monthlyRunCountGoal;
        private String preferredDifficulty;
        private Boolean autoPause;
        private String mapStyle;
        private Integer radius;
        private Integer paceUnit;
        private Boolean ttsEnabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Double dailyDistanceGoal;
        private Integer monthlyRunCountGoal;
        private String preferredDifficulty;
        private Boolean autoPause;
        private String mapStyle;
        private Integer radius;
        private Integer paceUnit;
        private Boolean ttsEnabled;

        public static Response from(Member member) {
            return Response.builder()
                    .dailyDistanceGoal(member.getDailyDistanceGoal())
                    .monthlyRunCountGoal(member.getMonthlyRunCountGoal())
                    .preferredDifficulty(member.getPreferredDifficulty())
                    .autoPause(member.getAutoPause())
                    .mapStyle(member.getMapStyle())
                    .radius(member.getRadius())
                    .paceUnit(member.getPaceUnit())
                    .ttsEnabled(member.getTtsEnabled())
                    .build();
        }
    }
}