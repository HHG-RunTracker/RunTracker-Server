package com.runtracker.domain.course.service;

import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Request;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.entity.converter.CoordinatesConverter;
import com.runtracker.domain.course.exception.CourseCreationFailedException;
import com.runtracker.domain.course.exception.CourseNotFoundException;
import com.runtracker.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            createCourseFromDTO(courseDTO);
        } catch (Exception e) {
            log.error("Failed to create free running course for member: {}, error: {}", 
                    courseDTO.getMemberId(), e.getMessage(), e);
            throw new CourseCreationFailedException();
        }
    }

    public Course createCourseFromDTO(CourseDTO courseDTO) {
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

            return courseRepository.save(course);
            
        } catch (Exception e) {
            log.error("Failed to create course from DTO for member: {}, error: {}", 
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

    @Transactional(readOnly = true)
    public CourseDetailDTO getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        return convertToCourseDetailDTO(course);
    }

    public CourseDetailDTO convertToCourseDetailDTO(Course course) {
        return CourseDetailDTO.builder()
                .id(course.getId())
                .memberId(course.getMemberId())
                .name(course.getName())
                .difficulty(course.getDifficulty())
                .points(course.getPoints())
                .startLat(course.getStartLat())
                .startLng(course.getStartLng())
                .distance(course.getDistance())
                .round(course.getRound())
                .region(course.getRegion())
                .photo(course.getPhoto())
                .photoLat(course.getPhotoLat())
                .photoLng(course.getPhotoLng())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}