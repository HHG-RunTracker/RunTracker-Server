package com.runtracker.domain.record.entity;

import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "crew_running_id")
    private Long crewRunningId;

    @Column(name = "running_time")
    private Integer runningTime;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "walk")
    private Integer walk;

    @Column(name = "calorie")
    private Integer calorie;
    
    public void updateFinishRunning(Integer runningTime, LocalDateTime finishedAt, Double distance, Integer walk, Integer calorie) {
        this.runningTime = runningTime;
        this.finishedAt = finishedAt;
        this.distance = distance;
        this.walk = walk;
        this.calorie = calorie;
    }
}