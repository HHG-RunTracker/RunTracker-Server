package com.runtracker.domain.crew.service;

import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.domain.crew.dto.CrewCourseRecommendationDTO;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.exception.CrewNotFoundException;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.record.repository.RecordRepository;
import com.runtracker.global.security.CrewAuthorizationUtil;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CrewRunningService {

    private final CrewRepository crewRepository;
    private final CourseRepository courseRepository;
    private final RecordRepository recordRepository;
    private final CrewAuthorizationUtil authorizationUtil;

    @Transactional(readOnly = true)
    public List<CrewCourseRecommendationDTO.Response> getRecommendedCourses(
            Long crewId, String region, Double minDistance, Double maxDistance, UserDetailsImpl userDetails) {

        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

        CrewCourseRecommendationDTO.Request request = CrewCourseRecommendationDTO.Request.builder()
                .region(region)
                .minDistance(minDistance)
                .maxDistance(maxDistance)
                .build();

        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .filter(course -> matchesRequest(course, request))
                .sorted((c1, c2) -> Boolean.compare(isCrewMatchingCourse(c2, crew), isCrewMatchingCourse(c1, crew)))
                .map(course -> mapToCourseRecommendation(course, crew))
                .limit(20)
                .toList();
    }

    private boolean matchesRequest(Course course, CrewCourseRecommendationDTO.Request request) {
        if (request.getRegion() != null && course.getRegion() != null &&
                !course.getRegion().contains(request.getRegion())) {
            return false;
        }

        if (request.getMinDistance() != null && course.getDistance() < request.getMinDistance()) {
            return false;
        }

        return request.getMaxDistance() == null || course.getDistance() <= request.getMaxDistance();
    }

    private CrewCourseRecommendationDTO.Response mapToCourseRecommendation(Course course, Crew crew) {
        return CrewCourseRecommendationDTO.Response.builder()
                .courseId(course.getId())
                .name(course.getName())
                .region(course.getRegion())
                .distance(course.getDistance())
                .difficulty(course.getDifficulty())
                .startLat(course.getStartLat())
                .startLng(course.getStartLng())
                .photo(course.getPhoto())
                .createdAt(course.getCreatedAt())
                .build();
    }

    private boolean isCrewMatchingCourse(Course course, Crew crew) {
        boolean regionMatch = crew.getRegion() != null && crew.getRegion().equals(course.getRegion());
        boolean difficultyMatch = crew.getDifficulty() != null && crew.getDifficulty().equals(course.getDifficulty());

        return regionMatch || difficultyMatch;
    }
}