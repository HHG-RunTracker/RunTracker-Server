package com.runtracker.domain.record.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecordDetailDTO {
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
    private String photo;
    private Double photoLat;
    private Double photoLng;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}