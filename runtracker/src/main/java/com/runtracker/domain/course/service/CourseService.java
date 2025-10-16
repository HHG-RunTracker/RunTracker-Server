package com.runtracker.domain.course.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseCreateDTO;
import com.runtracker.domain.course.dto.CourseUpdateDTO;
import com.runtracker.domain.course.service.dto.GoogleMapsDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.dto.FinishRunning;
import com.runtracker.domain.course.service.dto.RecommendationDTO;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.course.exception.AlreadyRunningException;
import com.runtracker.domain.course.exception.CourseCreationFailedException;
import com.runtracker.domain.course.exception.CourseNotFoundException;
import com.runtracker.domain.course.exception.CourseUpdateForbiddenException;
import com.runtracker.domain.course.exception.CourseDeleteForbiddenException;
import com.runtracker.domain.course.exception.InsufficientPathDataException;
import com.runtracker.domain.course.exception.InvalidStartTimeException;
import com.runtracker.domain.course.exception.MultipleActiveRunningException;
import com.runtracker.domain.course.exception.NoRecommendedCoursesException;
import com.runtracker.domain.course.exception.ValidationErrorException;
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
import com.runtracker.global.util.ImageUpload;
import com.runtracker.global.client.FastAPIClient;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final RouteAnalysisService routeAnalysisService;
    private final FastAPIClient fastAPIClient;
    private final ObjectMapper objectMapper;
    private final CourseCacheService courseCacheService;
    private final ImageUpload imageUploadHelper;

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
            validateCourseRequest(request);
            checkAlreadyRunning(memberId);

            Course course = createCourseFromRequest(memberId, request, false);
            Course savedCourse = courseRepository.save(course);

            createRunningRecord(memberId, savedCourse.getId());

            if (savedCourse.getStartLat() != null && savedCourse.getStartLng() != null) {
                courseCacheService.addCourseToSlot(savedCourse.getStartLat(), savedCourse.getStartLng(), savedCourse.getId());
            }

            return savedCourse;

        } catch (AlreadyRunningException | InsufficientPathDataException | ValidationErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to save course and start running for member: {}, error: {}",
                    memberId, e.getMessage(), e);
            throw new CourseCreationFailedException("Failed to save course and start running for member: " + memberId);
        }
    }

    public void saveTestCourse(Long memberId, CourseCreateDTO request) {
        try {
            validateCourseRequest(request);
            Course course = createCourseFromRequest(memberId, request, true);
            courseRepository.save(course);

        } catch (ValidationErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to save test course for member: {}, error: {}",
                    memberId, e.getMessage(), e);
            throw new CourseCreationFailedException("Failed to save test course for member: " + memberId);
        }
    }

    private void validateCourseRequest(CourseCreateDTO request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationErrorException("코스 이름은 필수입니다");
        }

        if (request.getName().length() > 100) {
            throw new ValidationErrorException("코스 이름은 100자 이하여야 합니다");
        }

        if (request.getPath() == null) {
            throw new ValidationErrorException("경로 정보는 필수입니다");
        }

        if (request.getDistance() == null || request.getDistance() <= 0) {
            throw new ValidationErrorException("거리는 0보다 커야 합니다");
        }

        if (request.getRound() == null) {
            throw new ValidationErrorException("런닝 왕복 정보는 필수입니다");
        }

        if (request.getRegion() == null || request.getRegion().trim().isEmpty()) {
            throw new ValidationErrorException("지역 정보는 필수입니다");
        }
    }

    private Course createCourseFromRequest(Long memberId, CourseCreateDTO request, boolean allowDummyData) {
        List<Coordinate> paths = request.getPath() != null ? request.getPath() : new ArrayList<>();

        Double startLat = null;
        Double startLng = null;

        if (!paths.isEmpty()) {
            Coordinate firstCoordinate = paths.get(0);
            startLat = firstCoordinate.getLat();
            startLng = firstCoordinate.getLnt();
        }

        Difficulty difficulty = calculateDifficulty(paths, allowDummyData);

        return Course.builder()
                .memberId(memberId)
                .name(request.getName())
                .difficulty(difficulty)
                .paths(paths)
                .startLat(startLat)
                .startLng(startLng)
                .distance(request.getDistance())
                .round(request.getRound() != null ? request.getRound() : false)
                .region(request.getRegion())
                .build();
    }

    private Difficulty calculateDifficulty(List<Coordinate> paths, boolean allowDummyData) {
        try {
            if (paths == null || paths.isEmpty()) {
                throw new InsufficientPathDataException();
            }

            GoogleMapsDTO.RouteAnalysisResult result = routeAnalysisService.analyzeRoute(paths);
            return result.difficulty();
        } catch (InsufficientPathDataException e) {
            if (allowDummyData) {
                // TODO: 더미데이터 코스 추가하기 위해 코스가 하나만 있으면 난이도 쉬움으로 저장. 나중에 에러처리로 교체 해야함.
                return Difficulty.EASY;
            } else {
                throw e;
            }
        } catch (Exception e) {
            log.error("Failed to calculate difficulty: {}", e.getMessage(), e);
            throw e;
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
    public List<Response> getNearbyCourses(Long memberId, Double latitude, Double longitude, Integer limit) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));

        List<Long> cachedCourseIds = courseCacheService.getNearbyCourseIds(latitude, longitude);
        if (cachedCourseIds != null) {
            return getCachedNearbyCourses(cachedCourseIds, latitude, longitude, member.getRadius());
        }

        Integer radius = member.getRadius();

        List<Response> courses = courseRepository.findNearbyCourses(
                latitude,
                longitude,
                radius
        );

        List<Long> courseIds = courses.stream().map(Response::getId).toList();
        courseCacheService.cacheNearbyCourses(latitude, longitude, courseIds);

        return courses;
    }

    private List<Response> getCachedNearbyCourses(List<Long> cachedCourseIds, Double latitude, Double longitude, Integer radius) {
        List<Response> result = new ArrayList<>();

        Map<Long, CourseDetailDTO> cachedDetails = courseCacheService.getMultipleCourseDetails(cachedCourseIds);

        for (Map.Entry<Long, CourseDetailDTO> entry : cachedDetails.entrySet()) {
            Response response = convertDetailToResponse(entry.getValue(), latitude, longitude);
            if (response.getDistanceFromUser() <= radius) {
                result.add(response);
            }
        }

        List<Long> missedIds = cachedCourseIds.stream()
                .filter(id -> !cachedDetails.containsKey(id))
                .toList();

        if (!missedIds.isEmpty()) {
            List<Course> courses = courseRepository.findAllById(missedIds);
            for (Course course : courses) {
                CourseDetailDTO detail = convertToCourseDetailDTO(course);
                courseCacheService.cacheCourseDetail(course.getId(), detail);

                Response response = convertDetailToResponse(detail, latitude, longitude);
                if (response.getDistanceFromUser() <= radius) {
                    result.add(response);
                }
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public CourseDetailDTO getCourseDetail(Long courseId) {
        CourseDetailDTO cached = courseCacheService.getCourseDetail(courseId);
        if (cached != null) {
            return cached;
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        CourseDetailDTO detail = convertToCourseDetailDTO(course);

        courseCacheService.cacheCourseDetail(courseId, detail);

        return detail;
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

    private Response convertDetailToResponse(CourseDetailDTO detail, Double userLat, Double userLng) {
        double distanceFromUser = calculateDistance(userLat, userLng, detail.getStartLat(), detail.getStartLng());

        return new Response(
                detail.getId(),
                detail.getMemberId(),
                detail.getName(),
                detail.getDifficulty(),
                detail.getPoints(),
                detail.getStartLat(),
                detail.getStartLng(),
                detail.getDistance(),
                detail.getRound(),
                detail.getRegion(),
                distanceFromUser
        );
    }

    private CourseDetailDTO convertResponseToCourseDetailDTO(Response response) {
        return CourseDetailDTO.builder()
                .id(response.getId())
                .memberId(response.getMemberId())
                .name(response.getName())
                .difficulty(response.getDifficulty())
                .points(response.getPoints())
                .startLat(response.getStartLat())
                .startLng(response.getStartLng())
                .distance(response.getDistance())
                .round(response.getRound())
                .region(response.getRegion())
                .build();
    }

    private double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return 0.0;
        }

        return routeAnalysisService.haversine(lat1, lng1, lat2, lng2);
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

        // Base64 이미지를 URL로 변환
        if (finishRunning.getPhoto() != null) {
            String photoUrl = imageUploadHelper.convertBase64ToUrlIfNeeded(finishRunning.getPhoto());
            finishRunning.setPhoto(photoUrl);
        }

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

    private List<Response> getNearbyCoursesWithCache(Double latitude, Double longitude, Integer radius) {
        List<Long> cachedCourseIds = courseCacheService.getNearbyCourseIds(latitude, longitude);

        if (cachedCourseIds != null) {
            return getCachedNearbyCourses(cachedCourseIds, latitude, longitude, radius);
        }

        List<Response> nearbyCourses = courseRepository.findNearbyCourses(latitude, longitude, radius);

        List<Long> courseIds = nearbyCourses.stream().map(Response::getId).toList();
        courseCacheService.cacheNearbyCourses(latitude, longitude, courseIds);

        for (Response course : nearbyCourses) {
            CourseDetailDTO detail = convertResponseToCourseDetailDTO(course);
            courseCacheService.cacheCourseDetail(course.getId(), detail);
        }

        return nearbyCourses;
    }

    @Transactional(readOnly = true)
    public List<CourseDetailDTO> getRecommendedCourses(Long memberId, Double latitude, Double longitude) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));

        List<RunningRecord> userRecords = recordRepository.findByMemberIdOrderByRunningTimeDesc(memberId);

        List<Response> nearbyCourses = getNearbyCoursesWithCache(latitude, longitude, member.getRadius());

        Set<Long> courseIds = userRecords.stream()
                .map(RunningRecord::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Course> courseMap = courseRepository.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        List<RecommendationDTO.UserRecord> userRecordDTOs = userRecords.stream()
                .filter(record -> record.getCourseId() != null)
                .map(record -> convertToUserRecordDTO(record, courseMap.get(record.getCourseId())))
                .filter(Objects::nonNull)
                .toList();

        List<RecommendationDTO.NearbyCourse> nearbyCourseDTOs = nearbyCourses.stream()
                .map(this::convertToNearbyCourseDTO)
                .toList();

        RecommendationDTO.Request request = RecommendationDTO.Request.builder()
                .userRecords(userRecordDTOs)
                .nearbyCourses(nearbyCourseDTOs)
                .build();

        List<Long> recommendedCourseIds = callFastAPIRecordRecommendation(request);

        return getRecommendedCourseDetails(recommendedCourseIds);
    }

    private List<Long> callFastAPIRecordRecommendation(RecommendationDTO.Request request) {
        return callFastAPI("/recommend/record", request);
    }

    private List<Long> callFastAPISettingRecommendation(RecommendationDTO.SettingRequest request) {
        return callFastAPI("/recommend/setting", request);
    }

    private List<Long> callFastAPI(String endpoint, Object request) {
        try {
            String rawResponse = fastAPIClient.post(endpoint, request, String.class);

            if (rawResponse == null || rawResponse.trim().isEmpty()) {
                log.warn("Empty response from FastAPI {}", endpoint);
                throw new NoRecommendedCoursesException();
            }

            List<RecommendationDTO.RecommendationItem> items = objectMapper.readValue(
                    rawResponse,
                    new TypeReference<>() {}
            );

            if (items == null || items.isEmpty()) {
                log.warn("No items in FastAPI response from {}", endpoint);
                throw new NoRecommendedCoursesException();
            }
            return items.stream()
                    .map(RecommendationDTO.RecommendationItem::getCourseId)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (NoRecommendedCoursesException e) {
            throw e;
        } catch (Exception e) {
            log.error("FastAPI {} request failed", endpoint, e);
            throw new NoRecommendedCoursesException();
        }
    }

    private RecommendationDTO.UserRecord convertToUserRecordDTO(RunningRecord record, Course course) {
        if (course == null) return null;

        return RecommendationDTO.UserRecord.builder()
                .courseId(record.getCourseId())
                .ranDistance(record.getDistance())
                .difficulty(course.getDifficulty().name())
                .latitude(course.getStartLat())
                .longitude(course.getStartLng())
                .build();
    }

    private RecommendationDTO.NearbyCourse convertToNearbyCourseDTO(Response course) {
        return RecommendationDTO.NearbyCourse.builder()
                .courseId(course.getId())
                .distance(course.getDistance())
                .difficulty(course.getDifficulty().name())
                .latitude(course.getStartLat())
                .longitude(course.getStartLng())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CourseDetailDTO> getRecommendedCoursesBySetting(Long memberId, Double latitude, Double longitude) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));

        List<Response> nearbyCourses = getNearbyCoursesWithCache(latitude, longitude, member.getRadius());

        List<RecommendationDTO.NearbyCourse> nearbyCourseDTOs = nearbyCourses.stream()
                .map(this::convertToNearbyCourseDTO)
                .toList();

        RecommendationDTO.UserSetting userSetting = RecommendationDTO.UserSetting.builder()
                .distance(member.getDailyDistanceGoal())
                .difficulty(member.getPreferredDifficulty())
                .build();

        RecommendationDTO.SettingRequest request = RecommendationDTO.SettingRequest.builder()
                .userSetting(userSetting)
                .nearbyCourses(nearbyCourseDTOs)
                .build();

        List<Long> recommendedCourseIds = callFastAPISettingRecommendation(request);

        return getRecommendedCourseDetails(recommendedCourseIds);
    }

    private List<CourseDetailDTO> getRecommendedCourseDetails(List<Long> recommendedCourseIds) {
        Map<Long, Course> recommendedCourseMap = courseRepository.findAllById(recommendedCourseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        return recommendedCourseIds.stream()
                .map(recommendedCourseMap::get)
                .filter(Objects::nonNull)
                .map(this::convertToCourseDetailDTO)
                .toList();
    }

    @Transactional
    public void updateCourse(Long memberId, Long courseId, CourseUpdateDTO courseUpdateDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        if (!course.getMemberId().equals(memberId)) {
            throw new CourseUpdateForbiddenException("You do not have permission to update this course");
        }

        Difficulty difficulty = null;
        if (courseUpdateDTO.getDifficulty() != null && !courseUpdateDTO.getDifficulty().trim().isEmpty()) {
            try {
                difficulty = Difficulty.valueOf(courseUpdateDTO.getDifficulty().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationErrorException("Invalid difficulty value: " + courseUpdateDTO.getDifficulty());
            }
        }

        course.updateCourse(courseUpdateDTO.getName(), difficulty);
        Course updatedCourse = courseRepository.save(course);

        CourseDetailDTO updatedDetail = convertToCourseDetailDTO(updatedCourse);
        courseCacheService.updateCourseDetail(courseId, updatedDetail);
    }

    @Transactional
    public void deleteCourse(Long memberId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));

        if (!course.getMemberId().equals(memberId)) {
            throw new CourseDeleteForbiddenException("You do not have permission to delete this course");
        }

        courseRepository.delete(course);

        courseCacheService.evictCourseDetail(courseId);
        if (course.getStartLat() != null && course.getStartLng() != null) {
            courseCacheService.removeCourseFromSlot(course.getStartLat(), course.getStartLng(), courseId);
        }
    }
}