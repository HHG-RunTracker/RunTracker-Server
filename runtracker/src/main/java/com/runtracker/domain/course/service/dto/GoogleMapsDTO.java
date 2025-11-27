package com.runtracker.domain.course.service.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.vo.Coordinate;
import java.util.List;

public class GoogleMapsDTO {

    public record ElevationResponse(
        List<ElevationResult> results,
        String status
    ) {}

    public record ElevationResult(
        double elevation,
        Coordinate location,
        double resolution
    ) {}

    public record RouteAnalysisResult(
        double totalDistanceM,
        double totalElevationGainM,
        double averageSlopePercent,
        double maxSlopePercent,
        Difficulty difficulty
    ) {}
}