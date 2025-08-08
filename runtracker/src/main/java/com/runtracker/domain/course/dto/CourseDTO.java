package com.runtracker.domain.course.dto;

import com.runtracker.domain.course.entity.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CourseDTO {
    private Long id;
    private Long memberId;
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