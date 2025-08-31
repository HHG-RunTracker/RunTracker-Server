package com.runtracker.domain.crew.entity;

import com.runtracker.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "crew_ranking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewRanking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "total_distance", nullable = false)
    private Double totalDistance;

    @Column(name = "total_running_time", nullable = false)
    private Integer totalRunningTime;

    @Column(name = "participant_count", nullable = false)
    private Integer participantCount;

    @Builder
    public CrewRanking(LocalDate date, Long crewId, Integer rankPosition, 
                      Double totalDistance, Integer totalRunningTime, Integer participantCount) {
        this.date = date;
        this.crewId = crewId;
        this.rankPosition = rankPosition;
        this.totalDistance = totalDistance;
        this.totalRunningTime = totalRunningTime;
        this.participantCount = participantCount;
    }

    public void updateRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }

    public void addCrewRecord(Double distance, Integer runningTime) {
        this.totalDistance += distance;
        this.totalRunningTime += runningTime;
        this.participantCount += 1;
    }

    public void updateTotalData(Double totalDistance, Integer totalRunningTime, Integer participantCount) {
        this.totalDistance = totalDistance;
        this.totalRunningTime = totalRunningTime;
        this.participantCount = participantCount;
    }
    
    public void updateTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public void updateTotalRunningTime(Integer totalRunningTime) {
        this.totalRunningTime = totalRunningTime;
    }
    
    public void updateParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }
}