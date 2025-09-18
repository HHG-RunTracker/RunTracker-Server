package com.runtracker.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunningMetaDTO {
    private Double distance;
    private Integer time;
    private Double avgPace;
    private Double avgSpeed;
}