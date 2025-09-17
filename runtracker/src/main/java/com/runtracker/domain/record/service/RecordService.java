package com.runtracker.domain.record.service;

import com.runtracker.domain.record.dto.RunningRecordDTO;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.record.exception.DateParameterRequiredException;
import com.runtracker.domain.record.exception.DateRangeTooLargeException;
import com.runtracker.domain.record.exception.InvalidDateRangeException;
import com.runtracker.domain.record.exception.InvalidSummaryTypeException;
import com.runtracker.domain.record.exception.RecordNotFoundException;
import com.runtracker.domain.record.repository.RecordRepository;
import com.runtracker.domain.course.entity.Course;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.global.vo.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {
    
    private final RecordRepository recordRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<RunningRecordDTO> getAllRunningRecords(Long memberId) {
        List<RunningRecord> records = recordRepository.findByMemberIdOrderByRunningTimeDesc(memberId);

        if (records.isEmpty()) {
            throw new RecordNotFoundException("No running records found for member " + memberId);
        }

        return records.stream()
                .map(RunningRecordDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RunningRecordDTO getRunningRecordById(Long memberId, Long recordId) {
        RunningRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RecordNotFoundException("Running record not found with id: " + recordId));

        if (!record.getMemberId().equals(memberId)) {
            throw new RecordNotFoundException("Running record not found with id: " + recordId);
        }

        List<Coordinate> calculatedPath = calculateFullPath(record);
        return RunningRecordDTO.fromWithCalculatedPath(record, calculatedPath);
    }

    private List<Coordinate> calculateFullPath(RunningRecord record) {
        List<Coordinate> fullPath = new ArrayList<>();

        if (record.getCourseId() != null) {
            Optional<Course> courseOpt = courseRepository.findById(record.getCourseId());
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                List<Coordinate> coursePaths = course.getPaths();
                List<Coordinate> lastCoursePath = record.getLastCoursePath();
                List<Coordinate> userFinishLocation = record.getUserFinishLocation();

                if (coursePaths != null && !coursePaths.isEmpty()) {
                    if (lastCoursePath != null && !lastCoursePath.isEmpty()) {
                        int lastIndex = findLastPathIndex(coursePaths, lastCoursePath);
                        if (lastIndex >= 0) {
                            fullPath.addAll(coursePaths.subList(0, lastIndex + 1));
                        } else {
                            fullPath.addAll(coursePaths);
                        }
                    } else {
                        fullPath.addAll(coursePaths);
                    }
                }

                if (userFinishLocation != null && !userFinishLocation.isEmpty()) {
                    fullPath.addAll(userFinishLocation);
                }
            }
        } else {
            if (record.getUserFinishLocation() != null) {
                fullPath.addAll(record.getUserFinishLocation());
            }
        }

        return fullPath;
    }

    private int findLastPathIndex(List<Coordinate> coursePaths, List<Coordinate> lastCoursePath) {
        if (lastCoursePath == null || lastCoursePath.isEmpty() || coursePaths == null || coursePaths.isEmpty()) {
            return -1;
        }

        Coordinate lastPoint = lastCoursePath.get(lastCoursePath.size() - 1);

        for (int i = coursePaths.size() - 1; i >= 0; i--) {
            Coordinate coursePoint = coursePaths.get(i);
            if (isCoordinateMatch(coursePoint, lastPoint)) {
                return i;
            }
        }

        return -1;
    }

    private boolean isCoordinateMatch(Coordinate coord1, Coordinate coord2) {
        if (coord1 == null || coord2 == null) {
            return false;
        }

        final double TOLERANCE = 0.0001;
        return Math.abs(coord1.getLat() - coord2.getLat()) < TOLERANCE &&
               Math.abs(coord1.getLnt() - coord2.getLnt()) < TOLERANCE;
    }

    @Transactional(readOnly = true)
    public List<RunningRecordDTO> getRunningRecordsSummary(Long memberId, String type, LocalDate date, LocalDate endDate) {
        List<RunningRecord> records;
        String lowerType = type.toLowerCase();

        if ("date".equals(lowerType)) {
            if (endDate == null) {
                throw new DateParameterRequiredException("End date is required for date range query");
            }
            records = getRecordsByDateRange(memberId, date, endDate);
        } else if ("week".equals(lowerType)) {
            records = getRecordsByWeek(memberId, date);
        } else if ("month".equals(lowerType)) {
            records = getRecordsByMonth(memberId, date);
        } else {
            throw new InvalidSummaryTypeException("Invalid type. Must be 'date', 'week', or 'month'");
        }

        if (records.isEmpty()) {
            throw new RecordNotFoundException("No running records found for member " + memberId + " for " + type);
        }

        return records.stream()
                .map(RunningRecordDTO::from)
                .collect(Collectors.toList());
    }

    private List<RunningRecord> getRecordsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return recordRepository.findByMemberIdAndRunningTimeBetween(memberId, startDate, endDate);
    }

    private List<RunningRecord> getRecordsByWeek(Long memberId, LocalDate weekDate) {
        validateWeekDate(weekDate);
        LocalDate weekStart = getWeekStart(weekDate);
        LocalDate weekEnd = getWeekEnd(weekDate);
        return recordRepository.findByMemberIdAndRunningTimeBetween(memberId, weekStart, weekEnd);
    }

    private List<RunningRecord> getRecordsByMonth(Long memberId, LocalDate monthDate) {
        validateMonthDate(monthDate);
        LocalDate monthStart = getMonthStart(monthDate);
        LocalDate monthEnd = getMonthEnd(monthDate);
        return recordRepository.findByMemberIdAndRunningTimeBetween(memberId, monthStart, monthEnd);
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
