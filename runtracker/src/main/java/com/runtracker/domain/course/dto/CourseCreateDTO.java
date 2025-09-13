package com.runtracker.domain.course.dto;

import com.runtracker.domain.course.entity.vo.Coordinate;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseCreateDTO {
    private String name;
    private List<Coordinate> path;
    private Double distance;
}