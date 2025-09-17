package com.runtracker.domain.crew.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.domain.crew.entity.CrewRanking;
import com.runtracker.global.code.DateConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CrewRankingDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrewRankInfo {
        private Long crewId;
        private String crewName;
        private String crewPhoto;
        private Double totalDistance;
        private Integer totalRunningTime;
        private Integer rank;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @JsonFormat(pattern = DateConstants.DATE_PATTERN)
        private LocalDate date;
        private List<CrewRankInfo> rankings;
        @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
        private LocalDateTime lastUpdated;
        
    }

}