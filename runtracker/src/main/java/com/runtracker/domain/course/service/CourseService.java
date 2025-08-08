package com.runtracker.domain.course.service;

import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;

    public void createFreeRunningCourse(CourseDTO courseDTO) {
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
    }
}