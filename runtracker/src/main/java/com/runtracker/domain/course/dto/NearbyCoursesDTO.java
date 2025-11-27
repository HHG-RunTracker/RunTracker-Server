package com.runtracker.domain.course.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.vo.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class NearbyCoursesDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Double latitude;
        private Double longitude;
        private Integer radius;
        private Integer limit;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long memberId;
        private String name;
        private Difficulty difficulty;
        private List<Coordinate> points;
        private Double startLat;
        private Double startLng;
        private Double distance;
        private Boolean round;
        private String region;
        private Double distanceFromUser;

        public Response(Long id, Long memberId, String name, Difficulty difficulty,
                       List<Coordinate> points, Double startLat, Double startLng,
                       Double distance, Boolean round, String region, Double distanceFromUser) {
            this.id = id;
            this.memberId = memberId;
            this.name = name;
            this.difficulty = difficulty;
            this.points = points;
            this.startLat = startLat;
            this.startLng = startLng;
            this.distance = distance;
            this.round = round;
            this.region = region;
            this.distanceFromUser = distanceFromUser;
        }
    }
}