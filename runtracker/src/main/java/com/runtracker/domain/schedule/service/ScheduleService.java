package com.runtracker.domain.schedule.service;

import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.global.security.UserDetailsImpl;
import com.runtracker.global.security.CrewAuthorizationUtil;
import com.runtracker.global.security.dto.CrewMembership;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.schedule.dto.ScheduleCreateDTO;
import com.runtracker.domain.schedule.dto.ScheduleDetailDTO;
import com.runtracker.domain.schedule.dto.ScheduleListDTO;
import com.runtracker.domain.schedule.dto.ScheduleParticipantDTO;
import com.runtracker.domain.schedule.dto.ScheduleUpdateDTO;
import com.runtracker.domain.schedule.entity.Schedule;
import com.runtracker.domain.schedule.enums.ScheduleErrorCode;
import com.runtracker.domain.schedule.exception.InvalidScheduleDateException;
import com.runtracker.domain.schedule.exception.ScheduleNotFoundException;
import com.runtracker.domain.schedule.exception.UnauthorizedScheduleAccessException;
import com.runtracker.domain.schedule.repository.ScheduleRepository;
import com.runtracker.global.code.DateConstants;
import com.runtracker.global.exception.CustomException;
import com.runtracker.domain.schedule.event.ScheduleCreateEvent;
import com.runtracker.domain.schedule.event.ScheduleUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final CrewAuthorizationUtil authorizationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createSchedule(ScheduleCreateDTO scheduleCreateDTO, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewManagementPermission(userDetails, scheduleCreateDTO.getCrewId());
        LocalDateTime parsedDate = parseAndValidateDate(scheduleCreateDTO.getDate());
        
        Schedule schedule = Schedule.builder()
                .crewId(scheduleCreateDTO.getCrewId())
                .memberId(userDetails.getMemberId())
                .date(parsedDate)
                .title(scheduleCreateDTO.getTitle())
                .content(scheduleCreateDTO.getContent())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        eventPublisher.publishEvent(new ScheduleCreateEvent(
            userDetails.getMemberId(),
            scheduleCreateDTO.getCrewId(),
            scheduleCreateDTO.getTitle()
        ));

        return savedSchedule.getId();
    }

    private LocalDateTime parseAndValidateDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new InvalidScheduleDateException("Date string is null or empty");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.SHORT_DATETIME_PATTERN);
            LocalDateTime parsedDate = LocalDateTime.parse(dateString, formatter);

            LocalDateTime now = LocalDateTime.now(ZoneId.of(DateConstants.TIME_ZONE));
            if (parsedDate.isBefore(now)) {
                throw new InvalidScheduleDateException("Schedule date cannot be in the past");
            }

            return parsedDate;
        } catch (InvalidScheduleDateException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidScheduleDateException("Invalid date format: " + dateString);
        }
    }

    @Transactional(readOnly = true)
    public ScheduleListDTO.ListResponse getCrewSchedules(Long crewId) {
        List<Schedule> schedules = scheduleRepository.findByCrewIdOrderByDateAsc(crewId);
        
        List<ScheduleListDTO.Response> scheduleResponses = schedules.stream()
                .map(schedule -> {
                    Member creator = memberRepository.findById(schedule.getMemberId())
                            .orElse(null);
                    String creatorName = creator != null ? creator.getName() : "알 수 없음";
                    return ScheduleListDTO.Response.from(schedule, creatorName);
                })
                .toList();
        
        return ScheduleListDTO.ListResponse.of(scheduleResponses);
    }

    @Transactional(readOnly = true)
    public ScheduleListDTO.ListResponse getCrewSchedulesByMemberId(UserDetailsImpl userDetails) {
        CrewMembership membership = userDetails.getCrewMembership();
        if (membership == null) {
            throw new UnauthorizedScheduleAccessException("User is not a member of any crew");
        }
        
        Long crewId = membership.getCrewId();
        authorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        return getCrewSchedules(crewId);
    }
    
    @Transactional(readOnly = true)
    public ScheduleDetailDTO.Response getScheduleDetail(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        authorizationUtil.validateCrewMemberAccess(userDetails, schedule.getCrewId());
        
        Member creator = memberRepository.findById(schedule.getMemberId())
                .orElse(null);
        String creatorName = creator != null ? creator.getName() : "알 수 없음";
        
        return ScheduleDetailDTO.Response.from(schedule, creatorName);
    }
    
    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleUpdateDTO scheduleUpdateDTO, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, schedule.getCrewId());

        LocalDateTime parsedDate = null;
        if (scheduleUpdateDTO.getDate() != null && !scheduleUpdateDTO.getDate().trim().isEmpty()) {
            parsedDate = parseAndValidateDate(scheduleUpdateDTO.getDate());
        }

        String titleToUpdate = scheduleUpdateDTO.getTitle() != null ? scheduleUpdateDTO.getTitle() : schedule.getTitle();

        schedule.updateSchedule(parsedDate, scheduleUpdateDTO.getTitle(), scheduleUpdateDTO.getContent());

        eventPublisher.publishEvent(new ScheduleUpdateEvent(
            userDetails.getMemberId(),
            schedule.getCrewId(),
            titleToUpdate
        ));
    }
    
    @Transactional
    public void deleteSchedule(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, schedule.getCrewId());

        scheduleRepository.delete(schedule);
    }
    
    @Transactional
    public void joinSchedule(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        
        authorizationUtil.validateCrewMemberAccess(userDetails, schedule.getCrewId());
        
        schedule.joinSchedule(userDetails.getMemberId());
    }
    
    @Transactional
    public void cancelSchedule(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        
        authorizationUtil.validateCrewMemberAccess(userDetails, schedule.getCrewId());
        
        schedule.cancelSchedule(userDetails.getMemberId());
    }
    
    @Transactional(readOnly = true)
    public ScheduleParticipantDTO.ListResponse getScheduleParticipants(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        
        authorizationUtil.validateCrewMemberAccess(userDetails, schedule.getCrewId());
        
        List<Long> participantIds = schedule.getParticipants();
        
        List<ScheduleParticipantDTO.Response> participantResponses = participantIds.stream()
                .map(participantId -> {
                    Member member = memberRepository.findById(participantId)
                            .orElse(null);
                    String memberName = member != null ? member.getName() : "알 수 없음";

                    MemberRole role = crewMemberRepository.findByCrewIdAndMemberId(schedule.getCrewId(), participantId)
                            .map(CrewMember::getRole)
                            .orElse(MemberRole.CREW_MEMBER);
                    
                    return ScheduleParticipantDTO.Response.builder()
                            .memberId(participantId)
                            .memberName(memberName)
                            .role(role)
                            .build();
                })
                .toList();
        
        return ScheduleParticipantDTO.ListResponse.of(participantResponses);
    }
    

}