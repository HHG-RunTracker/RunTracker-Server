package com.runtracker.domain.schedule.service;

import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.crew.service.CrewService;
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
import com.runtracker.domain.schedule.exception.ScheduleNotFoundException;
import com.runtracker.domain.schedule.repository.ScheduleRepository;
import com.runtracker.global.code.DateConstants;
import com.runtracker.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CrewService crewService;

    @Transactional
    public Long createSchedule(ScheduleCreateDTO scheduleCreateDTO, Long memberId) {
        crewService.validateCrewManagementPermission(scheduleCreateDTO.getCrewId(), memberId);
        LocalDateTime parsedDate = parseAndValidateDate(scheduleCreateDTO.getDate());
        
        Schedule schedule = Schedule.builder()
                .crewId(scheduleCreateDTO.getCrewId())
                .memberId(memberId)
                .date(parsedDate)
                .title(scheduleCreateDTO.getTitle())
                .content(scheduleCreateDTO.getContent())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return savedSchedule.getId();
    }

    private LocalDateTime parseAndValidateDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new CustomException(ScheduleErrorCode.INVALID_SCHEDULE_DATE);
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.SHORT_DATETIME_PATTERN);
            LocalDateTime parsedDate = LocalDateTime.parse(dateString, formatter);
            
            LocalDateTime now = LocalDateTime.now(ZoneId.of(DateConstants.TIME_ZONE));
            if (parsedDate.isBefore(now)) {
                throw new CustomException(ScheduleErrorCode.INVALID_SCHEDULE_DATE);
            }
            
            return parsedDate;
        } catch (Exception e) {
            throw new CustomException(ScheduleErrorCode.INVALID_SCHEDULE_DATE);
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
    public ScheduleListDTO.ListResponse getCrewSchedulesByMemberId(Long memberId) {
        CrewMember crewMember = crewMemberRepository.findByMemberIdAndStatus(memberId, 
                CrewMemberStatus.ACTIVE)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(ScheduleErrorCode.UNAUTHORIZED_SCHEDULE_ACCESS));

        return getCrewSchedules(crewMember.getCrewId());
    }
    
    @Transactional(readOnly = true)
    public ScheduleDetailDTO.Response getScheduleDetail(Long scheduleId, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        validateScheduleAccess(schedule.getCrewId(), memberId);
        
        Member creator = memberRepository.findById(schedule.getMemberId())
                .orElse(null);
        String creatorName = creator != null ? creator.getName() : "알 수 없음";
        
        return ScheduleDetailDTO.Response.from(schedule, creatorName);
    }
    
    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleUpdateDTO scheduleUpdateDTO, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        crewService.validateCrewManagementPermission(schedule.getCrewId(), memberId);

        LocalDateTime parsedDate = null;
        if (scheduleUpdateDTO.getDate() != null && !scheduleUpdateDTO.getDate().trim().isEmpty()) {
            parsedDate = parseAndValidateDate(scheduleUpdateDTO.getDate());
        }

        schedule.updateSchedule(parsedDate, scheduleUpdateDTO.getTitle(), scheduleUpdateDTO.getContent());
    }
    
    @Transactional
    public void deleteSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        crewService.validateCrewManagementPermission(schedule.getCrewId(), memberId);

        scheduleRepository.delete(schedule);
    }
    
    @Transactional
    public void joinSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        
        validateScheduleAccess(schedule.getCrewId(), memberId);
        
        schedule.joinSchedule(memberId);
    }
    
    @Transactional
    public void cancelSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        
        validateScheduleAccess(schedule.getCrewId(), memberId);
        
        schedule.cancelSchedule(memberId);
    }
    
    @Transactional(readOnly = true)
    public ScheduleParticipantDTO.ListResponse getScheduleParticipants(Long scheduleId, Long memberId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        
        validateScheduleAccess(schedule.getCrewId(), memberId);
        
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
    
    private void validateScheduleAccess(Long crewId, Long memberId) {
        boolean hasAccess = crewMemberRepository.findByMemberIdAndStatus(memberId, 
                CrewMemberStatus.ACTIVE)
                .stream()
                .anyMatch(crewMember -> crewMember.getCrewId().equals(crewId));
        
        if (!hasAccess) {
            throw new CustomException(ScheduleErrorCode.UNAUTHORIZED_SCHEDULE_ACCESS);
        }
    }

}