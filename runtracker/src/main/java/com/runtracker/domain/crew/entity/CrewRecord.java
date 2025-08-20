package com.runtracker.domain.crew.entity;

import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "crew_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrewRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crew_running_id", nullable = false, unique = true)
    private Long crewRunningId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "running_time")
    private Integer runningTime;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "walk")
    private Double walk;

    @Column(name = "calorie")
    private Double calorie;

    @Column(name = "participant_count")
    private Integer participantCount;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}