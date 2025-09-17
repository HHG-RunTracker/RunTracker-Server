package com.runtracker.domain.crew.entity;

import com.runtracker.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "crew_ranking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public void updateRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }
    
    public void updateTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public void updateTotalRunningTime(Integer totalRunningTime) {
        this.totalRunningTime = totalRunningTime;
    }
}