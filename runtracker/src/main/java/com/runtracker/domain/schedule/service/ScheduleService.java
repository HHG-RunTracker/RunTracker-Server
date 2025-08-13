package com.runtracker.domain.schedule.service;

import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.exception.NotCrewLeaderException;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.schedule.dto.ScheduleCreateDTO;
import com.runtracker.domain.schedule.entity.Schedule;
import com.runtracker.domain.schedule.enums.ScheduleErrorCode;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public Long createSchedule(ScheduleCreateDTO scheduleCreateDTO, Long memberId) {
        validateCrewManagementPermission(scheduleCreateDTO.getCrewId(), memberId);
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

    // TODO: 나중에 이 메소드 지우고 crew Service에서 상속 받기
    private void validateCrewManagementPermission(Long crewId, Long memberId) {
        CrewMember member = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(NotCrewLeaderException::new);

        if (member.getRole() != MemberRole.CREW_LEADER && member.getRole() != MemberRole.CREW_MANAGER) {
            throw new NotCrewLeaderException();
        }
    }
}