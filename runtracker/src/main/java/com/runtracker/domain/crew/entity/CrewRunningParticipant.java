package com.runtracker.domain.crew.entity;

import com.runtracker.domain.crew.enums.ParticipantStatus;
import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "crew_running_participant")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrewRunningParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crew_running_id", nullable = false)
    private Long crewRunningId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;


    public void joinRunning() {
        this.status = ParticipantStatus.JOINED;
        this.joinedAt = LocalDateTime.now();
    }

    public void startRunning() {
        this.status = ParticipantStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void finishRunning() {
        this.status = ParticipantStatus.FINISHED;
        this.finishedAt = LocalDateTime.now();
    }

    public void leaveRunning() {
        this.status = ParticipantStatus.LEFT;
    }

}