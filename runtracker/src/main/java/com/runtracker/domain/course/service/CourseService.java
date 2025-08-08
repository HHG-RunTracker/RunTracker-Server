package com.runtracker.domain.course.service;

import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Request;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.entity.converter.CoordinatesConverter;
import com.runtracker.domain.course.entity.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
import com.runtracker.domain.course.exception.CourseCreationFailedException;
import com.runtracker.domain.course.exception.CoordinatesParsingFailedException;
import com.runtracker.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final CoordinatesConverter coordinatesConverter = new CoordinatesConverter();

    public void createFreeRunningCourse(CourseDTO courseDTO) {
        try {
            Course course = Course.builder()
                    .memberId(courseDTO.getMemberId())
                    .name(courseDTO.getName())
                    .difficulty(courseDTO.getDifficulty())
                    .points(courseDTO.getPoints())
                    .startLat(courseDTO.getStartLat())
                    .startLng(courseDTO.getStartLng())
                    .distance(courseDTO.getDistance())
                    .round(courseDTO.getRound())
                    .indexs(courseDTO.getIndexs())
                    .region(courseDTO.getRegion())
                    .photo(courseDTO.getPhoto())
                    .photoLat(courseDTO.getPhotoLat())
                    .photoLng(courseDTO.getPhotoLng())
                    .build();

            courseRepository.save(course);
            
        } catch (Exception e) {
            log.error("Failed to create free running course for member: {}, error: {}", 
                    courseDTO.getMemberId(), e.getMessage(), e);
            throw new CourseCreationFailedException("Failed to create course for member: " + courseDTO.getMemberId());
        }
    }

    @Transactional(readOnly = true)
    public List<Response> getNearbyCourses(Request request) {
        return courseRepository.findNearbyCourses(
                request.getLatitude(),
                request.getLongitude()
        );
    }
}