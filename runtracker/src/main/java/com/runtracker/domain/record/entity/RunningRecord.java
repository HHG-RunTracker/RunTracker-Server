package com.runtracker.domain.record.entity;

import com.runtracker.global.entity.BaseEntity;
import com.runtracker.global.vo.Coordinate;
import com.runtracker.global.vo.SegmentPace;
import com.runtracker.global.converter.CoordinatesConverter;
import com.runtracker.global.converter.SegmentPacesConverter;
import com.runtracker.global.converter.SegmentPathsConverter;
import com.runtracker.domain.course.dto.FinishRunning;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Integer runningTime;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column
    private String photo;

    @Column(name = "is_goal_achieved", columnDefinition = "TINYINT(1)")
    private Boolean isGoalAchieved;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "avgPace")
    private Double avgPace;

    @Column(name = "avgSpeed")
    private Double avgSpeed;

    @Column(name = "kcal")
    private Integer kcal;

    @Column(name = "walkCnt")
    private Integer walkCnt;

    @Column(name = "avgHeartRate")
    private Integer avgHeartRate;

    @Column(name = "maxHeartRate")
    private Integer maxHeartRate;

    @Column(name = "avgCadence")
    private Integer avgCadence;

    @Column(name = "maxCadence")
    private Integer maxCadence;

    @Convert(converter = CoordinatesConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<Coordinate> userFinishLocation = new ArrayList<>();

    @Convert(converter = CoordinatesConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<Coordinate> lastCoursePath = new ArrayList<>();

    @Convert(converter = SegmentPacesConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<SegmentPace> segmentPaces = new ArrayList<>();

    @Convert(converter = SegmentPathsConverter.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<List<Coordinate>> segmentPaths = new ArrayList<>();

    public void updateFinishRunning(Integer runningTime, LocalDateTime finishedAt, FinishRunning finishRunning) {
        this.runningTime = runningTime;
        this.startedAt = finishRunning.getStartedAt();
        this.finishedAt = finishedAt;
        this.distance = finishRunning.getDistance();
        this.avgPace = finishRunning.getAvgPace();
        this.avgSpeed = finishRunning.getAvgSpeed();
        this.kcal = finishRunning.getKcal();
        this.walkCnt = finishRunning.getWalkCnt();
        this.avgHeartRate = finishRunning.getAvgHeartRate();
        this.maxHeartRate = finishRunning.getMaxHeartRate();
        this.avgCadence = finishRunning.getAvgCadence();
        this.maxCadence = finishRunning.getMaxCadence();
        this.userFinishLocation = finishRunning.getUserFinishLocation() != null ? finishRunning.getUserFinishLocation() : new ArrayList<>();
        this.lastCoursePath = finishRunning.getLastCoursePath() != null ? finishRunning.getLastCoursePath() : new ArrayList<>();
        this.segmentPaces = finishRunning.getSegmentPaces() != null ? finishRunning.getSegmentPaces() : new ArrayList<>();
        this.segmentPaths = finishRunning.getSegmentPaths() != null ? finishRunning.getSegmentPaths() : new ArrayList<>();
        this.photo = finishRunning.getPhoto();
        this.isGoalAchieved = finishRunning.getIsGoalAchieved();
    }
}