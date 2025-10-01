package com.runtracker.domain.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

public class RecommendationDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @JsonProperty("user_records")
        private List<UserRecord> userRecords;

        @JsonProperty("nearby_courses")
        private List<NearbyCourse> nearbyCourses;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRecord {
        @JsonProperty("course_id")
        private Long courseId;

        @JsonProperty("ran_distance")
        private Double ranDistance;

        @JsonProperty("difficulty")
        private String difficulty;

        @JsonProperty("latitude")
        private Double latitude;

        @JsonProperty("longitude")
        private Double longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NearbyCourse {
        @JsonProperty("course_id")
        private Long courseId;

        @JsonProperty("distance")
        private Double distance;

        @JsonProperty("difficulty")
        private String difficulty;

        @JsonProperty("latitude")
        private Double latitude;

        @JsonProperty("longitude")
        private Double longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("recommended_course_ids")
        private List<Long> recommendedCourseIds;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationItem {
        @JsonProperty("course_id")
        private Long courseId;

        @JsonProperty("similarity")
        private Double similarity;
    }
}