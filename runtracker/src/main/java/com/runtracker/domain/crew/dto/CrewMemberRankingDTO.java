package com.runtracker.domain.crew.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.global.code.DateConstants;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CrewMemberRankingDTO {

    @Getter
    @Builder
    public static class Response {
        @JsonFormat(pattern = DateConstants.DATE_PATTERN)
        private LocalDate date;
        private Long crewId;
        private String crewName;
        private List<MemberRankInfo> rankings;
        @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
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
        private Double averageDistance;
        private Integer averageRunningTime;
    }

}