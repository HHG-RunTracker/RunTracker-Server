package com.runtracker.domain.crew.entity;

import com.runtracker.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "crew_member_ranking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMemberRanking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "total_distance", nullable = false)
    private Double totalDistance;

    @Column(name = "total_running_time", nullable = false)
    private Integer totalRunningTime;

    @Column(name = "participation_count", nullable = false)
    private Integer participationCount;

    @Builder
    public CrewMemberRanking(LocalDate date, Long crewId, Long memberId, Integer rankPosition,
                            Double totalDistance, Integer totalRunningTime, Integer participationCount) {
        this.date = date;
        this.crewId = crewId;
        this.memberId = memberId;
        this.rankPosition = rankPosition;
        this.totalDistance = totalDistance;
        this.totalRunningTime = totalRunningTime;
        this.participationCount = participationCount;
    }

    public void updateRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }

    public void addRunningRecord(Double distance, Integer runningTime) {
        this.totalDistance += distance;
        this.totalRunningTime += runningTime;
        this.participationCount += 1;
    }

    public void updateTotalData(Double totalDistance, Integer totalRunningTime, Integer participationCount) {
        this.totalDistance = totalDistance;
        this.totalRunningTime = totalRunningTime;
        this.participationCount = participationCount;
    }
}