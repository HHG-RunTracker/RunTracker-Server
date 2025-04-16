package com.runtracker_prototype.domain;

import com.runtracker_prototype.domain.attr.Coordinate;
import com.runtracker_prototype.domain.converter.*;
import com.runtracker_prototype.domain.menu.Difficulty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Course {
    @Id
    @GeneratedValue
    @Column(name = "course_id")
    private Long id;

    private String name; // 코스 이름

    private String photoPath; // 코스 사진 경로(AWS S3)

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty; // 난이도 [초급자 : EASY, 중급자 : MEDIUM, 전문가 : HARD]

    private Boolean isCircle; // 왕복 유무

    @Convert(converter = CoordinateConverter.class)
    @Column(columnDefinition = "json")
    private Coordinate startCoordinate; // 시작점 좌표

    @Convert(converter = CoordinatesConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<Coordinate> points = new ArrayList<>(); // 코스 좌표 리스트
}
