package com.runtracker.domain.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.global.code.DateConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Integer walk;
    private Integer calorie;
    
    public static RunningRecordDTO from(RunningRecord runningRecord) {
        return new RunningRecordDTO(
                runningRecord.getId(),
                runningRecord.getCourseId(),
                runningRecord.getRunningTime(),
                runningRecord.getStartedAt(),
                runningRecord.getFinishedAt(),
                runningRecord.getDistance(),
                runningRecord.getWalk(),
                runningRecord.getCalorie()
        );
    }
}