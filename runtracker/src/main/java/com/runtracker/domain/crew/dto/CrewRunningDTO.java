package com.runtracker.domain.crew.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
import com.runtracker.domain.crew.enums.CrewRunningStatus;
import com.runtracker.domain.crew.enums.ParticipantStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CrewRunningDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String title;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StartRunningWithCourseRequest {
        private Long courseId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StartFreeRunningRequest {
        private String name;
        private Difficulty difficulty;
        private List<Coordinate> points;
        private Double startLat;
        private Double startLng;
        private Double distance;
        private Boolean round;
        private String indexs;
        private String region;
        private String photo;
        private Double photoLat;
        private Double photoLng;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long crewId;
        private Long creatorId;
        private String creatorName;
        private CrewRunningStatus status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String title;
        private String description;
        private List<ParticipantInfo> participants;
        private LocalDateTime createdAt;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParticipantInfo {
        private Long memberId;
        private String memberName;
        private ParticipantStatus status;
        private LocalDateTime joinedAt;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
    }
}