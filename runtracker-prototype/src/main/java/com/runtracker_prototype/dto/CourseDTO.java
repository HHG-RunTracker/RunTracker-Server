package com.runtracker_prototype.dto;

import com.runtracker_prototype.domain.attr.Coordinate;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CourseDTO {
    private Long id; // 코스 id
    private String name; // 코스 이름
    private String photoPath; // 사진 경로
    private String difficulty; // 난이도
    private Coordinate startCoordinate; // 시작점 좌표
    private List<Coordinate> points = new ArrayList<>(); // 코스 좌표 리스트
}