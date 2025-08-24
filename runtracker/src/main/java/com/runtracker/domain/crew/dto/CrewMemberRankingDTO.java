package com.runtracker.domain.crew.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CrewMemberRankingDTO {

    @Getter
    @Builder
    public static class Response {
        private LocalDate date;
        private Long crewId;
        private String crewName;
        private List<MemberRankInfo> rankings;
        private LocalDateTime lastUpdated;
    }

    @Getter
    @Builder
    public static class MemberRankInfo {
        private Long memberId;
        private String memberName;
        private String memberPhoto;
        private Integer rank;
        private Double totalDistance;
        private Integer totalRunningTime;
        private Integer participationCount;
        private Double averageDistance;
        private Integer averageRunningTime;
    }

}