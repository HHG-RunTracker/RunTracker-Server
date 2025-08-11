package com.runtracker.domain.record.service;

import com.runtracker.domain.record.dto.RecordDetailDTO;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.record.exception.CourseNotFoundForRecordException;
import com.runtracker.domain.record.exception.InvalidDateRangeException;
import com.runtracker.domain.record.exception.DateRangeTooLargeException;
import com.runtracker.domain.record.exception.FutureDateNotAllowedException;
import com.runtracker.domain.record.exception.DateParameterRequiredException;
import com.runtracker.domain.record.exception.NoRecordsFoundException;
import com.runtracker.domain.record.repository.RecordRepository;
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
    
    @Transactional(readOnly = true)
    public RecordDetailDTO getCourseDetail(Long courseId) {
        Course course = recordRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundForRecordException("Course not found with id: " + courseId));

        return convertToRecordDetailDTO(course);
    }

    @Transactional(readOnly = true)
    public List<RecordDetailDTO> getCoursesByDate(Long memberId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        List<Course> courses = recordRepository.findByMemberIdAndCreatedAtBetween(memberId, startDate, endDate);
        
        if (courses.isEmpty()) {
            throw new NoRecordsFoundException("No courses found for member " + memberId + " between " + startDate + " and " + endDate);
        }
        
        return courses.stream()
                .map(this::convertToRecordDetailDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecordDetailDTO> getCoursesByWeek(Long memberId, LocalDate weekDate) {
        validateWeekDate(weekDate);
        
        LocalDate weekStart = getWeekStart(weekDate);
        LocalDate weekEnd = getWeekEnd(weekDate);
        
        List<Course> courses = recordRepository.findByMemberIdAndCreatedAtBetween(memberId, weekStart, weekEnd);
        
        if (courses.isEmpty()) {
            throw new NoRecordsFoundException("No courses found for member " + memberId + " for the week of " + weekDate);
        }
        
        return courses.stream()
                .map(this::convertToRecordDetailDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecordDetailDTO> getCoursesByMonth(Long memberId, LocalDate monthDate) {
        validateMonthDate(monthDate);
        
        LocalDate monthStart = getMonthStart(monthDate);
        LocalDate monthEnd = getMonthEnd(monthDate);
        
        List<Course> courses = recordRepository.findByMemberIdAndCreatedAtBetween(memberId, monthStart, monthEnd);
        
        if (courses.isEmpty()) {
            throw new NoRecordsFoundException("No courses found for member " + memberId + " for the month of " + monthDate.getYear() + "-" + monthDate.getMonthValue());
        }
        
        return courses.stream()
                .map(this::convertToRecordDetailDTO)
                .collect(Collectors.toList());
    }

    private RecordDetailDTO convertToRecordDetailDTO(Course course) {
        return RecordDetailDTO.builder()
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

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new DateParameterRequiredException("Both startDate and endDate are required");
        }
        
        LocalDate today = LocalDate.now();

        if (startDate.isAfter(today)) {
            throw new FutureDateNotAllowedException("Start date cannot be in the future: " + startDate);
        }
        if (endDate.isAfter(today)) {
            throw new FutureDateNotAllowedException("End date cannot be in the future: " + endDate);
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
        
        LocalDate today = LocalDate.now();
        if (weekDate.isAfter(today)) {
            throw new FutureDateNotAllowedException("Week date cannot be in the future: " + weekDate);
        }
    }

    private void validateMonthDate(LocalDate monthDate) {
        if (monthDate == null) {
            throw new DateParameterRequiredException("Month date parameter is required");
        }
        
        LocalDate today = LocalDate.now();
        if (monthDate.isAfter(today)) {
            throw new FutureDateNotAllowedException("Month date cannot be in the future: " + monthDate);
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
