package com.runtracker.domain.course.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.vo.Coordinate;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseCreateDTO {
    private String name;
    private Difficulty difficulty;
    private List<Coordinate> path;
    private Double distance;
    private Boolean round;
    private String region;
}