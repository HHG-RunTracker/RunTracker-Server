package com.runtracker.domain.record.service;

import com.runtracker.domain.record.dto.RunningRecordDTO;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.record.exception.CourseNotFoundForRecordException;
import com.runtracker.domain.record.exception.InvalidDateRangeException;
import com.runtracker.domain.record.exception.DateRangeTooLargeException;
import com.runtracker.domain.record.exception.DateParameterRequiredException;
import com.runtracker.domain.record.exception.NoRecordsFoundException;
import com.runtracker.domain.record.repository.RecordRepository;
import com.runtracker.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {
    
    private final RecordRepository recordRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void saveRunningRecord(Long memberId, RunningRecordDTO createDTO) {
        // 코스가 존재하는지 확인
        if (!courseRepository.existsById(createDTO.getCourseId())) {
            throw new CourseNotFoundForRecordException("Course not found with id: " + createDTO.getCourseId());
        }
        
        RunningRecord runningRecord = RunningRecord.builder()
                .memberId(memberId)
                .courseId(createDTO.getCourseId())
                .runningTime(createDTO.getRunningTime())
                .distance(createDTO.getDistance())
                .walk(createDTO.getWalk())
                .calorie(createDTO.getCalorie())
                .build();
        
        recordRepository.save(runningRecord);
    }

    @Transactional(readOnly = true)
    public List<RunningRecordDTO> getRunningRecordsByDate(Long memberId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        List<RunningRecord> records = recordRepository.findByMemberIdAndRunningTimeBetween(memberId, startDate, endDate);
        
        if (records.isEmpty()) {
            throw new NoRecordsFoundException("No running records found for member " + memberId + " between " + startDate + " and " + endDate);
        }
        
        return records.stream()
                .map(RunningRecordDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RunningRecordDTO> getRunningRecordsByWeek(Long memberId, LocalDate weekDate) {
        validateWeekDate(weekDate);
        
        LocalDate weekStart = getWeekStart(weekDate);
        LocalDate weekEnd = getWeekEnd(weekDate);
        
        List<RunningRecord> records = recordRepository.findByMemberIdAndRunningTimeBetween(memberId, weekStart, weekEnd);
        
        if (records.isEmpty()) {
            throw new NoRecordsFoundException("No running records found for member " + memberId + " for the week of " + weekDate);
        }
        
        return records.stream()
                .map(RunningRecordDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RunningRecordDTO> getRunningRecordsByMonth(Long memberId, LocalDate monthDate) {
        validateMonthDate(monthDate);
        
        LocalDate monthStart = getMonthStart(monthDate);
        LocalDate monthEnd = getMonthEnd(monthDate);
        
        List<RunningRecord> records = recordRepository.findByMemberIdAndRunningTimeBetween(memberId, monthStart, monthEnd);
        
        if (records.isEmpty()) {
            throw new NoRecordsFoundException("No running records found for member " + memberId + " for the month of " + monthDate.getYear() + "-" + monthDate.getMonthValue());
        }
        
        return records.stream()
                .map(RunningRecordDTO::from)
                .collect(Collectors.toList());
    }


    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new DateParameterRequiredException("Both startDate and endDate are required");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before or equal to end date. Start: " + startDate + ", End: " + endDate);
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 365) {
            throw new DateRangeTooLargeException("Date range cannot exceed 365 days. Current range: " + daysBetween + " days");
        }
    }

    private void validateWeekDate(LocalDate weekDate) {
        if (weekDate == null) {
            throw new DateParameterRequiredException("Week date parameter is required");
        }
        
    }

    private void validateMonthDate(LocalDate monthDate) {
        if (monthDate == null) {
            throw new DateParameterRequiredException("Month date parameter is required");
        }
        
    }

    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getWeekEnd(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    public static LocalDate getMonthStart(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getMonthEnd(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }
}
