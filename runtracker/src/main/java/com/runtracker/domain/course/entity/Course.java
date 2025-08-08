package com.runtracker.domain.course.entity;

import com.runtracker.domain.course.entity.converter.CoordinatesConverter;
import com.runtracker.domain.course.entity.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Convert(converter = CoordinatesConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<Coordinate> points = new ArrayList<>();

    @Column(name = "start_lat")
    private Double startLat;

    @Column(name = "start_lng")
    private Double startLng;

    private Double distance;

    @Column(name = "round")
    private Boolean round;

    @Column(columnDefinition = "json")
    private String indexs;

    @Column(length = 100)
    private String region;

    @Column(length = 255)
    private String photo;

    @Column(name = "photo_lat")
    private Double photoLat;

    @Column(name = "photo_lng")
    private Double photoLng;
}