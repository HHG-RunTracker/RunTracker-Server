package com.runtracker.domain.course.entity;

import com.runtracker.global.converter.CoordinatesConverter;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.vo.Coordinate;
import com.runtracker.global.entity.BaseEntity;
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
@Table(name = "course")
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Convert(converter = CoordinatesConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<Coordinate> paths = new ArrayList<>();

    @Column(name = "start_lat")
    private Double startLat;

    @Column(name = "start_lng")
    private Double startLng;

    private Double distance;

    @Column(name = "round", columnDefinition = "TINYINT(1)")
    private Boolean round;

    @Column(length = 100)
    private String region;

    public void updateCourse(String name, Difficulty difficulty) {
        if (name != null) {
            this.name = name;
        }
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
    }
}