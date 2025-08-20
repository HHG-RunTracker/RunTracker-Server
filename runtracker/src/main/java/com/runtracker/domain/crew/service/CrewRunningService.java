package com.runtracker.domain.crew.service;

import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.domain.course.service.CourseService;
import com.runtracker.domain.crew.dto.CrewCourseRecommendationDTO;
import com.runtracker.domain.crew.dto.CrewRunningDTO;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewRunning;
import com.runtracker.domain.crew.entity.CrewRunningParticipant;
import com.runtracker.domain.crew.enums.CrewRunningStatus;
import com.runtracker.domain.crew.enums.ParticipantStatus;
import com.runtracker.domain.crew.exception.*;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.crew.repository.CrewRunningRepository;
import com.runtracker.domain.crew.repository.CrewRunningParticipantRepository;
import com.runtracker.domain.member.repository.MemberRepository;
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
    private final CourseService courseService;
    private final CrewRunningRepository crewRunningRepository;
    private final CrewRunningParticipantRepository crewRunningParticipantRepository;
    private final MemberRepository memberRepository;
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

    private void startAllParticipants(Long crewRunningId) {
        List<CrewRunningParticipant> participants = crewRunningParticipantRepository
                .findByCrewRunningIdOrderByJoinedAtAsc(crewRunningId);

        for (CrewRunningParticipant participant : participants) {
            participant.startRunning();
        }
        crewRunningParticipantRepository.saveAll(participants);
    }

    private void createAndSaveParticipant(Long crewRunningId, Long memberId) {
        CrewRunningParticipant participant = CrewRunningParticipant.builder()
                .crewRunningId(crewRunningId)
                .memberId(memberId)
                .status(ParticipantStatus.JOINED)
                .build();
        participant.joinRunning();

        crewRunningParticipantRepository.save(participant);
    }

    private void validateUserNotInActiveCrewRunning(Long memberId) {
        List<CrewRunningParticipant> activeParticipations = crewRunningParticipantRepository
                .findByMemberIdAndStatusIn(memberId, List.of(ParticipantStatus.JOINED, ParticipantStatus.RUNNING));

        for (CrewRunningParticipant participation : activeParticipations) {
            CrewRunning crewRunning = crewRunningRepository.findById(participation.getCrewRunningId())
                    .orElse(null);
            
            if (crewRunning != null && 
                (crewRunning.getStatus() == CrewRunningStatus.WAITING || 
                 crewRunning.getStatus() == CrewRunningStatus.IN_PROGRESS)) {
                throw new AlreadyInActiveCrewRunningException();
            }
        }
    }

    public void createCrewRunning(Long crewId, CrewRunningDTO.CreateRequest request, UserDetailsImpl userDetails) {
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

        validateUserNotInActiveCrewRunning(userDetails.getMemberId());

        try {
            // 크루 런닝 방 생성
            CrewRunning crewRunning = CrewRunning.builder()
                    .crewId(crewId)
                    .courseId(null)
                    .creatorId(userDetails.getMemberId())
                    .status(CrewRunningStatus.WAITING)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .build();

            CrewRunning savedCrewRunning = crewRunningRepository.save(crewRunning);

            // 크루 런닝방 생성 한 사람을 러닝 참여자로 추가
            createAndSaveParticipant(savedCrewRunning.getId(), userDetails.getMemberId());

        } catch (Exception e) {
            throw new CrewRunningCreationFailedException("Failed to create crew running for crew: " + crewId);
        }
    }

    @Transactional(readOnly = true)
    public List<CrewRunningDTO.Response> getCrewRunnings(Long crewId, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        List<CrewRunning> crewRunnings = crewRunningRepository.findByCrewIdOrderByCreatedAtDesc(crewId);

        return crewRunnings.stream()
                .map(this::convertToCrewRunningResponseWithDetails)
                .toList();
    }

    public void joinCrewRunning(Long crewId, Long crewRunningId, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        CrewRunning crewRunning = crewRunningRepository.findByIdAndCrewId(crewRunningId, crewId)
                .orElseThrow(CrewRunningNotFoundException::new);

        if (crewRunning.getStatus() != CrewRunningStatus.WAITING) {
            throw new CrewRunningNotWaitingException();
        }

        boolean alreadyJoined = crewRunningParticipantRepository
                .existsByCrewRunningIdAndMemberId(crewRunningId, userDetails.getMemberId());

        if (alreadyJoined) {
            throw new AlreadyJoinedCrewRunningException();
        }

        validateUserNotInActiveCrewRunning(userDetails.getMemberId());

        createAndSaveParticipant(crewRunningId, userDetails.getMemberId());
    }

    public void leaveCrewRunning(Long crewId, Long crewRunningId, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        CrewRunning crewRunning = crewRunningRepository.findByIdAndCrewId(crewRunningId, crewId)
                .orElseThrow(CrewRunningNotFoundException::new);

        if (crewRunning.getStatus() != CrewRunningStatus.WAITING) {
            throw new CannotLeaveStartedRunningException();
        }

        CrewRunningParticipant participant = crewRunningParticipantRepository
                .findByCrewRunningIdAndMemberId(crewRunningId, userDetails.getMemberId())
                .orElseThrow(NotJoinedCrewRunningException::new);

        crewRunningParticipantRepository.delete(participant);
    }

    private CrewRunningDTO.Response convertToCrewRunningResponse(CrewRunning crewRunning, List<CrewRunningParticipant> participants) {
        String creatorName = memberRepository.findById(crewRunning.getCreatorId())
                .map(member -> member.getName())
                .orElse("알 수 없음");

        return CrewRunningDTO.Response.builder()
                .id(crewRunning.getId())
                .crewId(crewRunning.getCrewId())
                .creatorId(crewRunning.getCreatorId())
                .creatorName(creatorName)
                .status(crewRunning.getStatus())
                .startTime(crewRunning.getStartTime())
                .endTime(crewRunning.getEndTime())
                .title(crewRunning.getTitle())
                .description(crewRunning.getDescription())
                .participants(participants.stream()
                        .map(p -> {
                            String memberName = memberRepository.findById(p.getMemberId())
                                    .map(member -> member.getName())
                                    .orElse("알 수 없음");

                            return CrewRunningDTO.ParticipantInfo.builder()
                                    .memberId(p.getMemberId())
                                    .memberName(memberName)
                                    .status(p.getStatus())
                                    .joinedAt(p.getJoinedAt())
                                    .startedAt(p.getStartedAt())
                                    .finishedAt(p.getFinishedAt())
                                    .build();
                        })
                        .toList())
                .createdAt(crewRunning.getCreatedAt())
                .build();
    }

    private CrewRunningDTO.Response convertToCrewRunningResponseWithDetails(CrewRunning crewRunning) {
        List<CrewRunningParticipant> participants = crewRunningParticipantRepository
                .findByCrewRunningIdOrderByJoinedAtAsc(crewRunning.getId());

        return convertToCrewRunningResponse(crewRunning, participants);
    }

    @Transactional(readOnly = true)
    public CrewRunningDTO.Response getCrewRunningDetail(Long crewId, Long crewRunningId, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        CrewRunning crewRunning = crewRunningRepository.findByIdAndCrewId(crewRunningId, crewId)
                .orElseThrow(CrewRunningNotFoundException::new);

        return convertToCrewRunningResponseWithDetails(crewRunning);
    }

    public void deleteCrewRunning(Long crewId, Long crewRunningId, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

        CrewRunning crewRunning = crewRunningRepository.findByIdAndCrewId(crewRunningId, crewId)
                .orElseThrow(CrewRunningNotFoundException::new);

        if (crewRunning.getStatus() != CrewRunningStatus.WAITING) {
            throw new CannotDeleteStartedRunningException();
        }

        List<CrewRunningParticipant> participants = crewRunningParticipantRepository
                .findByCrewRunningIdOrderByJoinedAtAsc(crewRunningId);
        crewRunningParticipantRepository.deleteAll(participants);

        crewRunningRepository.delete(crewRunning);
    }

    public CourseDetailDTO startCrewRunningWithCourse(Long crewId, Long crewRunningId, CrewRunningDTO.StartRunningWithCourseRequest request, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

        CrewRunning crewRunning = crewRunningRepository.findByIdAndCrewId(crewRunningId, crewId)
                .orElseThrow(CrewRunningNotFoundException::new);

        if (crewRunning.getStatus() != CrewRunningStatus.WAITING) {
            throw new CrewRunningNotWaitingException();
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CrewRunningCreationFailedException("Course not found: " + request.getCourseId()));

        try {
            crewRunning.setCourseId(request.getCourseId());
            crewRunning.startRunning();
            crewRunningRepository.save(crewRunning);

            // 모든 참여자 상태를 RUNNING으로 변경
            startAllParticipants(crewRunningId);

            return courseService.convertToCourseDetailDTO(course);

        } catch (Exception e) {
            throw new CrewRunningCreationFailedException("Failed to start crew running with course: " + e.getMessage());
        }
    }

    public void startCrewFreeRunning(Long crewId, Long crewRunningId, CourseDTO courseDTO, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

        CrewRunning crewRunning = crewRunningRepository.findByIdAndCrewId(crewRunningId, crewId)
                .orElseThrow(CrewRunningNotFoundException::new);

        if (crewRunning.getStatus() != CrewRunningStatus.WAITING) {
            throw new CrewRunningNotWaitingException();
        }

        try {
            courseDTO.setMemberId(userDetails.getMemberId());
            Course savedCourse = courseService.createCourseFromDTO(courseDTO);

            crewRunning.setCourseId(savedCourse.getId());
            crewRunning.startRunning();
            crewRunningRepository.save(crewRunning);

            startAllParticipants(crewRunningId);

        } catch (Exception e) {
            throw new CrewRunningCreationFailedException("Failed to start crew free running: " + e.getMessage());
        }
    }
}