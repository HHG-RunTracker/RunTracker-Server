package com.runtracker.domain.crew.dto;

import com.runtracker.domain.course.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CrewCourseRecommendationDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String region;
        private Double minDistance;
        private Double maxDistance;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long courseId;
        private String name;
        private String region;
        private Double distance;
        private Difficulty difficulty;
        private Double startLat;
        private Double startLng;
        private String photo;
        private LocalDateTime createdAt;
    }
}