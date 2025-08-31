package com.runtracker.domain.crew.entity;

import com.runtracker.domain.crew.enums.CrewRunningStatus;
import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "crew_running")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrewRunning extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CrewRunningStatus status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public void startRunning() {
        this.status = CrewRunningStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
    }

    public void finishRunning() {
        this.status = CrewRunningStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }
}