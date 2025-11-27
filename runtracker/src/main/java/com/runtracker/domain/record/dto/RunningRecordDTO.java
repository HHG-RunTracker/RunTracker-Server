package com.runtracker.domain.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.global.code.DateConstants;
import com.runtracker.global.vo.Coordinate;
import com.runtracker.global.vo.SegmentPace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunningRecordDTO {

    private Long id;
    private Long courseId;

    private Integer runningTime;

    @DateTimeFormat(pattern = DateConstants.DATETIME_PATTERN)
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime startedAt;

    @DateTimeFormat(pattern = DateConstants.DATETIME_PATTERN)
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime finishedAt;

    private Double distance;
    private Double avgPace;
    private Double avgSpeed;
    private Integer kcal;
    private Integer walkCnt;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Integer avgCadence;
    private Integer maxCadence;
    private List<Coordinate> path;
    private List<SegmentPace> segmentPaces;
    private List<List<Coordinate>> segmentPaths;

    public static RunningRecordDTO from(RunningRecord runningRecord) {
        return new RunningRecordDTO(
                runningRecord.getId(),
                runningRecord.getCourseId(),
                runningRecord.getRunningTime(),
                runningRecord.getStartedAt(),
                runningRecord.getFinishedAt(),
                runningRecord.getDistance(),
                runningRecord.getAvgPace(),
                runningRecord.getAvgSpeed(),
                runningRecord.getKcal(),
                runningRecord.getWalkCnt(),
                runningRecord.getAvgHeartRate(),
                runningRecord.getMaxHeartRate(),
                runningRecord.getAvgCadence(),
                runningRecord.getMaxCadence(),
                // summary나 유저 기록 전체 조회에선 경로 제공하지 않음 (기록 상세 보기에서만 제공)
                null,
                null,
                null
        );
    }

    public static RunningRecordDTO fromWithCalculatedPath(RunningRecord runningRecord, List<Coordinate> calculatedPath) {
        return new RunningRecordDTO(
                runningRecord.getId(),
                runningRecord.getCourseId(),
                runningRecord.getRunningTime(),
                runningRecord.getStartedAt(),
                runningRecord.getFinishedAt(),
                runningRecord.getDistance(),
                runningRecord.getAvgPace(),
                runningRecord.getAvgSpeed(),
                runningRecord.getKcal(),
                runningRecord.getWalkCnt(),
                runningRecord.getAvgHeartRate(),
                runningRecord.getMaxHeartRate(),
                runningRecord.getAvgCadence(),
                runningRecord.getMaxCadence(),
                calculatedPath != null ? calculatedPath : new ArrayList<>(),
                runningRecord.getSegmentPaces() != null ? runningRecord.getSegmentPaces() : new ArrayList<>(),
                runningRecord.getSegmentPaths() != null ? runningRecord.getSegmentPaths() : new ArrayList<>()
        );
    }

}