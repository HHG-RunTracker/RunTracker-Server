package com.runtracker.domain.crew.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.domain.crew.entity.CrewRecord;
import com.runtracker.global.code.DateConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewRecordDTO {
    
    private Long id;
    private Long crewRunningId;
    private Long courseId;
    private Integer runningTime;
    
    @DateTimeFormat(pattern = DateConstants.DATETIME_PATTERN)
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime startedAt;
    
    @DateTimeFormat(pattern = DateConstants.DATETIME_PATTERN)
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime finishedAt;
    
    private Double distance;
    private Double walk;
    private Double calorie;
    private Integer participantCount;
    
    public static CrewRecordDTO from(CrewRecord crewRecord) {
        return new CrewRecordDTO(
                crewRecord.getId(),
                crewRecord.getCrewRunningId(),
                crewRecord.getCourseId(),
                crewRecord.getRunningTime(),
                crewRecord.getStartedAt(),
                crewRecord.getFinishedAt(),
                crewRecord.getDistance(),
                crewRecord.getWalk(),
                crewRecord.getCalorie(),
                crewRecord.getParticipantCount()
        );
    }
}