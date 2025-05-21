package com.runtracker_prototype.service;

import com.runtracker_prototype.domain.Course;
import com.runtracker_prototype.domain.attr.Coordinate;
import com.runtracker_prototype.domain.menu.Difficulty;
import com.runtracker_prototype.dto.CourseDTO;
import com.runtracker_prototype.dto.NearbyCourses;
import com.runtracker_prototype.errorCode.CourseErrorCode;
import com.runtracker_prototype.exception.CourseCreationFailedException;
import com.runtracker_prototype.exception.CustomException;
import com.runtracker_prototype.exception.DifficultyRequiredException;
import com.runtracker_prototype.exception.InvalidDifficultyException;
import com.runtracker_prototype.repository.CourseRepository;
import com.runtracker_prototype.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    
    private final CourseRepository courseRepository;

    @Transactional
    public CourseDTO createCustomCourse(CourseDTO courseDTO) {
        // 기본 유효성 검사
        if (courseDTO.getPoints() == null || courseDTO.getPoints().isEmpty()) {
            throw new CustomException(CourseErrorCode.INVALID_COURSE_DATA);
        }

        // 시작 좌표가 없으면 첫 번째 포인트를 시작 좌표로 설정
        if (courseDTO.getStartCoordinate() == null) {
            courseDTO.setStartCoordinate(courseDTO.getPoints().get(0));
        }

        // difficulty 검증
        if (courseDTO.getDifficulty() == null) {
            throw new DifficultyRequiredException();
        }

        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(courseDTO.getDifficulty());
        } catch (IllegalArgumentException e) {
            throw new InvalidDifficultyException();
        }

        try {
            // 코스 생성
            Course course = Course.builder()
                    .name(courseDTO.getName() != null ? courseDTO.getName() : "자유 러닝 코스")
                    .points(courseDTO.getPoints())
                    .startCoordinate(courseDTO.getStartCoordinate())
                    .difficulty(difficulty)
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

    public List<CourseDTO> getNearbyCourses(NearbyCourses request) {
        // 현재 위치 좌표 생성
        Coordinate currentLocation = new Coordinate(request.getLatitude(), request.getLongitude());
        
        // 모든 코스 조회
        List<Course> allCourses = courseRepository.findAll();
        
        // 반경 내의 코스만 필터링하고 거리순으로 정렬
        List<CourseDTO> nearbyCourses = allCourses.stream()
                .map(course -> {
                    // 코스까지의 거리 계산
                    double distance = GeoUtils.calculateDistance(currentLocation, course.getStartCoordinate());
                    return new CourseWithDistance(course, distance);
                })
                .filter(courseWithDistance -> courseWithDistance.distance <= request.getRadiusInMeters())
                .sorted((c1, c2) -> Double.compare(c1.distance, c2.distance))
                .map(courseWithDistance -> {
                    Course course = courseWithDistance.course;
                    return new CourseDTO(
                            course.getId(),
                            course.getName(),
                            course.getPhotoPath(),
                            course.getDifficulty().name(),
                            course.getStartCoordinate(),
                            course.getPoints()
                    );
                })
                .collect(Collectors.toList());

        // 주변 코스가 없으면 예외 발생
        if (nearbyCourses.isEmpty()) {
            throw new CustomException(CourseErrorCode.NO_COURSES_FOUND);
        }

        return nearbyCourses;
    }

    // 거리 계산을 위한 내부 클래스
    private static class CourseWithDistance {
        private final Course course;
        private final double distance;

        public CourseWithDistance(Course course, double distance) {
            this.course = course;
            this.distance = distance;
        }
    }
}
