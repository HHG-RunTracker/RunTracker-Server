package com.runtracker.domain.course.service;

import com.runtracker.domain.course.service.dto.GoogleMapsDTO;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.course.exception.GoogleMapsApiException;
import com.runtracker.domain.course.exception.InsufficientPathDataException;
import com.runtracker.domain.course.exception.NoPathDataException;
import com.runtracker.global.vo.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RouteAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.maps.api-key}")
    private String apiKey;

    private static final String ELEVATION_API_URL = "https://maps.googleapis.com/maps/api/elevation/json?locations={locations}&key={apiKey}";

    private static final double EASY_AVG_SLOPE_THRESHOLD = 3.0;
    private static final double EASY_MAX_SLOPE_THRESHOLD = 7.0;
    private static final double EASY_ELEVATION_THRESHOLD = 200.0;
    private static final double MEDIUM_AVG_SLOPE_THRESHOLD = 6.0;
    private static final double MEDIUM_MAX_SLOPE_THRESHOLD = 12.0;
    private static final double MEDIUM_ELEVATION_THRESHOLD = 600.0;

    private static final int EARTH_RADIUS_M = 6371000;
    private static final int MIN_ELEVATION_POINTS = 2;

    public GoogleMapsDTO.RouteAnalysisResult analyzeCourse(Course course) {
        if (course.getPaths() == null || course.getPaths().isEmpty()) {
            throw new NoPathDataException();
        }

        return analyzeRoute(course.getPaths());
    }

    public GoogleMapsDTO.RouteAnalysisResult analyzeRoute(List<Coordinate> coords) {
        if (coords == null || coords.isEmpty()) {
            throw new NoPathDataException();
        }

        if (coords.size() < MIN_ELEVATION_POINTS) {
            throw new InsufficientPathDataException();
        }

        String locationsParam = coords.stream()
                .map(coord -> coord.getLat() + "," + coord.getLnt())
                .collect(Collectors.joining("|"));

        GoogleMapsDTO.ElevationResponse response;
        try {
            response = restTemplate.getForObject(ELEVATION_API_URL, GoogleMapsDTO.ElevationResponse.class, locationsParam, apiKey);

            if (response == null || !"OK".equals(response.status()) ||
                response.results() == null || response.results().size() < MIN_ELEVATION_POINTS) {
                log.error("Google Maps API error - Status: {}, Results count: {}",
                    response != null ? response.status() : "null",
                    response != null && response.results() != null ? response.results().size() : "null");
                throw new GoogleMapsApiException();
            }
        } catch (GoogleMapsApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google Maps API call failed", e);
            throw new GoogleMapsApiException();
        }

        List<GoogleMapsDTO.ElevationResult> elevationData = response.results();
        double totalDistance = 0;
        double totalElevationGain = 0;
        double maxSlope = 0;

        for (int i = 0; i < elevationData.size() - 1; i++) {
            GoogleMapsDTO.ElevationResult start = elevationData.get(i);
            GoogleMapsDTO.ElevationResult end = elevationData.get(i+1);

            if (start == null || end == null) {
                log.error("Null elevation result at index {} or {}", i, i+1);
                continue;
            }

            if (start.location() == null || end.location() == null) {
                log.error("Null location at index {} or {}", i, i+1);
                continue;
            }

            double distance = haversine(start.location().getLat(), start.location().getLnt(), end.location().getLat(), end.location().getLnt());
            if (distance == 0) {
                log.warn("Zero distance calculated for segment {}", i + 1);
                continue;
            }

            totalDistance += distance;
            double elevationChange = end.elevation() - start.elevation();

            if (elevationChange > 0) {
                totalElevationGain += elevationChange;
            }

            double currentSlope = (elevationChange / distance) * 100;
            if (currentSlope > maxSlope) {
                maxSlope = currentSlope;
            }
        }

        double averageSlope = (totalDistance > 0) ? (totalElevationGain / totalDistance) * 100 : 0;
        Difficulty difficulty = determineDifficulty(averageSlope, maxSlope, totalElevationGain);

        return new GoogleMapsDTO.RouteAnalysisResult(totalDistance, totalElevationGain, averageSlope, maxSlope, difficulty);
    }

    private Difficulty determineDifficulty(double avgSlope, double maxSlope, double elevationGain) {
        if (avgSlope < EASY_AVG_SLOPE_THRESHOLD && maxSlope < EASY_MAX_SLOPE_THRESHOLD && elevationGain < EASY_ELEVATION_THRESHOLD) {
            return Difficulty.EASY;
        }
        if (avgSlope < MEDIUM_AVG_SLOPE_THRESHOLD && maxSlope < MEDIUM_MAX_SLOPE_THRESHOLD && elevationGain < MEDIUM_ELEVATION_THRESHOLD) {
            return Difficulty.MEDIUM;
        }
        return Difficulty.HARD;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_M * c;
    }
}