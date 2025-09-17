package com.runtracker.domain.course.service;

import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseCreateDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Request;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.dto.FinishRunning;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.exception.AlreadyRunningException;
import com.runtracker.domain.course.exception.CourseCreationFailedException;
import com.runtracker.domain.course.exception.CourseNotFoundException;
import com.runtracker.domain.course.exception.InvalidStartTimeException;
import com.runtracker.domain.course.exception.MultipleActiveRunningException;
import com.runtracker.domain.crew.service.CrewRankingService;
import com.runtracker.domain.crew.service.CrewMemberRankingService;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.exception.MemberNotFoundException;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.member.service.TempCalcService;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.record.exception.CourseNotFoundForRecordException;
import com.runtracker.domain.record.exception.RecordNotFoundException;
import com.runtracker.domain.record.repository.RecordRepository;
import com.runtracker.global.vo.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final CrewRankingService crewRankingService;
    private final CrewMemberRankingService crewMemberRankingService;
    private final CrewMemberRepository crewMemberRepository;

    private void checkAlreadyRunning(Long memberId) {
        List<RunningRecord> activeRecords = recordRepository.findAllByMemberIdAndFinishedAtIsNull(memberId);
        if (!activeRecords.isEmpty()) {
            throw new AlreadyRunningException("Member already has active running record");
        }
    }

    private void createRunningRecord(Long memberId, Long courseId) {
        RunningRecord runningRecord = RunningRecord.builder()
                .memberId(memberId)
                .courseId(courseId)
                .build();

        recordRepository.save(runningRecord);
    }

    public Course saveCourse(Long memberId, CourseCreateDTO request) {
        try {
            checkAlreadyRunning(memberId);

            List<Coordinate> paths = request.getPath() != null ? request.getPath() : new ArrayList<>();

            Double startLat = null;
            Double startLng = null;

            if (!paths.isEmpty()) {
                Coordinate firstCoordinate = paths.get(0);
                startLat = firstCoordinate.getLat();
                startLng = firstCoordinate.getLnt();
            }

            Course course = Course.builder()
                    .memberId(memberId)
                    .name(request.getName())
                    .difficulty(request.getDifficulty())
                    .paths(paths)
                    .startLat(startLat)
                    .startLng(startLng)
                    .distance(request.getDistance())
                    .round(request.getRound() != null ? request.getRound() : false)
                    .region(request.getRegion())
                    .build();

            Course savedCourse = courseRepository.save(course);

            createRunningRecord(memberId, savedCourse.getId());

            return savedCourse;

        } catch (AlreadyRunningException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to save course and start running for member: {}, error: {}",
                    memberId, e.getMessage(), e);
            throw new CourseCreationFailedException("Failed to save course and start running for member: " + memberId);
        }
    }

    public void startRunningCourse(Long memberId, Long courseId) {
        checkAlreadyRunning(memberId);

        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        try {
            createRunningRecord(memberId, courseId);
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
                .points(course.getPaths())
                .startLat(course.getStartLat())
                .startLng(course.getStartLng())
                .distance(course.getDistance())
                .round(course.getRound())
                .region(course.getRegion())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    @Transactional
    public void finishRunning(Long memberId, FinishRunning finishRunning) {
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
        LocalDateTime startedAt = finishRunning.getStartedAt();

        if (startedAt.isAfter(finishedAt)) {
            throw new InvalidStartTimeException();
        }

        long runningTimeSeconds = Duration.between(startedAt, finishedAt).getSeconds();

        Long courseId = existingRecord.getCourseId();

        existingRecord.updateFinishRunning(
                (int) runningTimeSeconds,
                finishedAt,
                finishRunning
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

        try {
            List<CrewMember> memberCrew = crewMemberRepository.findByMemberIdAndStatus(
                memberId, CrewMemberStatus.ACTIVE);

            if (!memberCrew.isEmpty()) {
                Long crewId = memberCrew.get(0).getCrewId();
                LocalDate today = LocalDate.now();

                crewRankingService.recalculateRanking(today);
                crewMemberRankingService.recalculateCrewMemberRanking(crewId, today);
            }
        } catch (Exception e) {
            log.warn("Failed to recalculate rankings after finishing running for member {}: {}",
                     memberId, e.getMessage());
        }
    }
}