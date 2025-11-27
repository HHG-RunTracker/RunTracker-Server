package com.runtracker.domain.course.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.vo.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailDTO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}