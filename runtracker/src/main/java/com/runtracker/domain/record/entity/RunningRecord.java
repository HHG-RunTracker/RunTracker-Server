package com.runtracker.domain.record.entity;

import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "running_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RunningRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "running_time")
    private LocalDateTime runningTime;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "walk")
    private Integer walk;

    @Column(name = "calorie")
    private Integer calorie;
}