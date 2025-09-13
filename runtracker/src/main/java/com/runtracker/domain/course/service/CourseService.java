package com.runtracker.domain.course.service;

import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseCreateDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Request;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.dto.FinishRunning;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.exception.AlreadyRunningException;
import com.runtracker.domain.course.exception.CourseCreationFailedException;
import com.runtracker.domain.course.exception.CourseNotFoundException;
import com.runtracker.domain.course.exception.MultipleActiveRunningException;
import com.runtracker.domain.course.exception.ValidationException;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.exception.MemberNotFoundException;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.member.service.TempCalcService;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.record.exception.CourseNotFoundForRecordException;
import com.runtracker.domain.record.exception.RecordNotFoundException;
import com.runtracker.domain.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;
    private final TempCalcService temperatureCalculationService;



    private void validateFinishRunning(FinishRunning finishRunning) {
        if (finishRunning.getDistance() == null || finishRunning.getDistance() < 0) {
            throw new ValidationException("Distance is required and must be non-negative");
        }
        
        if (finishRunning.getWalk() == null || finishRunning.getWalk() < 0) {
            throw new ValidationException("Walk is required and must be non-negative");
        }
        
        if (finishRunning.getCalorie() == null || finishRunning.getCalorie() < 0) {
            throw new ValidationException("Calorie is required and must be non-negative");
        }
    }

    private void checkAlreadyRunning(Long memberId) {
        List<RunningRecord> activeRecords = recordRepository.findAllByMemberIdAndFinishedAtIsNull(memberId);
        if (!activeRecords.isEmpty()) {
            throw new AlreadyRunningException("Member already has active running record");
        }
    }

    public Course saveCourse(Long memberId, CourseCreateDTO request) {
        try {
            Course course = Course.builder()
                    .memberId(memberId)
                    .name(request.getName())
                    .points(request.getPath())
                    .distance(request.getDistance())
                    .build();

            return courseRepository.save(course);
            
        } catch (Exception e) {
            log.error("Failed to save course for member: {}, error: {}", 
                    memberId, e.getMessage(), e);
            throw new CourseCreationFailedException("Failed to save course for member: " + memberId);
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

    public void startRunningCourse(Long memberId, Long courseId) {
        checkAlreadyRunning(memberId);

        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        try {
            RunningRecord runningRecord = RunningRecord.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .runningTime(null)
                    .startedAt(LocalDateTime.now())
                    .finishedAt(null)
                    .distance(null)
                    .walk(null)
                    .calorie(null)
                    .build();

            recordRepository.save(runningRecord);
        } catch (Exception e) {
            throw new CourseCreationFailedException();
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

    @Transactional
    public void finishRunning(Long memberId, FinishRunning finishRunning) {
        validateFinishRunning(finishRunning);

        // Todo: 나중에 런닝 상태 관리를 제대로 하고 싶으면 개인 러닝 상태관리 테이블을 하나 만들기 (findAllByMemberIdAndFinishedAtIsNull 대신에)
        List<RunningRecord> activeRecords = recordRepository.findAllByMemberIdAndFinishedAtIsNull(memberId);
        
        if (activeRecords.isEmpty()) {
            throw new RecordNotFoundException("No active running record found for member: " + memberId);
        }
        
        if (activeRecords.size() > 1) {
            throw new MultipleActiveRunningException("Multiple active running records found for member: " + memberId);
        }
        
        RunningRecord existingRecord = activeRecords.get(0);

        LocalDateTime finishedAt = LocalDateTime.now();
        LocalDateTime startedAt = existingRecord.getStartedAt();
        
        long runningTimeSeconds = Duration.between(startedAt, finishedAt).getSeconds();
        
        Long courseId = existingRecord.getCourseId();
        
        existingRecord.updateFinishRunning(
                (int) runningTimeSeconds,
                finishedAt,
                finishRunning.getDistance(),
                finishRunning.getWalk(),
                finishRunning.getCalorie()
        );

        recordRepository.save(existingRecord);

        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseNotFoundForRecordException("Course not found with id: " + courseId));

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));

            double newTemperature = temperatureCalculationService.calculateNewTemperature(
                    member.getTemperature(), finishRunning.getDistance(), course.getDistance());

            double roundedTemperature = Math.round(newTemperature * 10.0) / 10.0;

            member.updateTemperature(roundedTemperature);
            memberRepository.save(member);
        }
    }
}