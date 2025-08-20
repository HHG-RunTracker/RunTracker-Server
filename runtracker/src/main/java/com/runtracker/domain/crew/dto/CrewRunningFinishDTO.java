package com.runtracker.domain.crew.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewRunningFinishDTO {
    
    private Double distance;
    private Integer walk;
    private Integer calorie;
}