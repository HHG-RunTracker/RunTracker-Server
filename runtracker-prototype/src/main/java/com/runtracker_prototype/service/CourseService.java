package com.runtracker_prototype.service;

import com.runtracker_prototype.domain.Course;
import com.runtracker_prototype.domain.menu.Difficulty;
import com.runtracker_prototype.dto.CourseDTO;
import com.runtracker_prototype.errorCode.CourseErrorCode;
import com.runtracker_prototype.exception.CourseCreationFailedException;
import com.runtracker_prototype.exception.CustomException;
import com.runtracker_prototype.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    
    private final CourseRepository courseRepository;

    @Transactional
    public CourseDTO createCustomCourse(CourseDTO courseDTO) {
        try {
            if (courseDTO.getPoints() == null || courseDTO.getPoints().isEmpty()) {
                throw new CustomException(CourseErrorCode.INVALID_COURSE_DATA);
            }

            // 시작 좌표가 없으면 첫 번째 포인트를 시작 좌표로 설정
            if (courseDTO.getStartCoordinate() == null) {
                courseDTO.setStartCoordinate(courseDTO.getPoints().get(0));
            }

            // 코스 생성
            Course course = Course.builder()
                    .name(courseDTO.getName() != null ? courseDTO.getName() : "자유 러닝 코스")
                    .points(courseDTO.getPoints())
                    .startCoordinate(courseDTO.getStartCoordinate())
                    .difficulty(Difficulty.EASY)
                    .isCircle(false)
                    .build();

            Course savedCourse = courseRepository.save(course);

            // DTO 변환
            return new CourseDTO(
                    savedCourse.getId(),
                    savedCourse.getName(),
                    savedCourse.getPhotoPath(),
                    savedCourse.getDifficulty().name(),
                    savedCourse.getStartCoordinate(),
                    savedCourse.getPoints()
            );
        } catch (Exception e) {
            throw new CourseCreationFailedException();
        }
    }

    public CourseDTO getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(CourseErrorCode.COURSE_NOT_FOUND));

        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getPhotoPath(),
                course.getDifficulty().name(),
                course.getStartCoordinate(),
                course.getPoints()
        );
    }
}
