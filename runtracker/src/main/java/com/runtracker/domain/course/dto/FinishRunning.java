package com.runtracker.domain.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.global.code.DateConstants;
import com.runtracker.global.vo.Coordinate;
import com.runtracker.global.vo.SegmentPace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishRunning {
    @DateTimeFormat(pattern = DateConstants.DATETIME_PATTERN)
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime startedAt;
    private Double distance;
    private Double avgPace;
    private Double avgSpeed;
    private Integer kcal;
    private Integer walkCnt;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Integer avgCadence;
    private Integer maxCadence;
    private List<Coordinate> userFinishLocation;
    private List<Coordinate> lastCoursePath;
    private List<SegmentPace> segmentPaces;
    private List<List<Coordinate>> segmentPaths;
    private String photo;
    private Boolean isGoalAchieved;
}