package com.runtracker.domain.crew.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewRankingCacheDTO {
    private final Double totalDistance;
    private final Integer totalRunningTime;
}