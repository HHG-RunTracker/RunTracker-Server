package com.runtracker_prototype.service;

import com.runtracker_prototype.code.DateConstants;
import com.runtracker_prototype.domain.Course;
import com.runtracker_prototype.domain.Record;
import com.runtracker_prototype.dto.RecordDTO;
import com.runtracker_prototype.errorCode.CourseErrorCode;
import com.runtracker_prototype.errorCode.RecordErrorCode;
import com.runtracker_prototype.exception.CustomException;
import com.runtracker_prototype.repository.CourseRepository;
import com.runtracker_prototype.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {
    
    private final RecordRepository recordRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public RecordDTO saveRecord(RecordDTO recordDTO) {
        validateRecord(recordDTO);
        
        Course course = courseRepository.findById(recordDTO.getCourseId())
                .orElseThrow(() -> new CustomException(RecordErrorCode.COURSE_NOT_FOUND_FOR_RECORD));

        // 현재 시간을 Asia/Seoul 기준으로 설정
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of(DateConstants.TIME_ZONE));

        Record record = Record.builder()
                .course(course)
                .time(currentTime)
                .kcal(recordDTO.getKcal())
                .walkCnt(recordDTO.getWalkCnt())
                .build();

        Record savedRecord = recordRepository.save(record);
        
        return new RecordDTO(
                savedRecord.getId(),
                savedRecord.getCourse().getId(),
                savedRecord.getTime(),
                savedRecord.getKcal(),
                savedRecord.getWalkCnt()
        );
    }

    private void validateRecord(RecordDTO recordDTO) {
        if (recordDTO.getCourseId() == null) {
            throw new CustomException(RecordErrorCode.RECORD_COURSE_ID_REQUIRED);
        }
        if (recordDTO.getKcal() == null) {
            throw new CustomException(RecordErrorCode.RECORD_KCAL_REQUIRED);
        }
        if (recordDTO.getWalkCnt() == null) {
            throw new CustomException(RecordErrorCode.RECORD_WALK_COUNT_REQUIRED);
        }
    }

    private LocalDateTime parseDateTime(LocalDateTime dateTime) {
        try {
            return dateTime.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of(DateConstants.TIME_ZONE))
                    .toLocalDateTime();
        } catch (DateTimeParseException e) {
            throw new CustomException(RecordErrorCode.INVALID_DATETIME_FORMAT);
        }
    }

    public List<RecordDTO> getAllRecords() {
        List<Record> records = recordRepository.findAllByOrderByTimeDesc();
        
        if (records.isEmpty()) {
            throw new CustomException(RecordErrorCode.NO_RECORDS_FOUND);
        }

        return records.stream()
                .map(record -> new RecordDTO(
                        record.getId(),
                        record.getCourse().getId(),
                        record.getTime(),
                        record.getKcal(),
                        record.getWalkCnt()
                ))
                .collect(Collectors.toList());
    }

    public List<RecordDTO> getRecordsByCourseId(Long courseId) {
        // 코스 존재 여부 확인
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.COURSE_NOT_FOUND_FOR_RECORD));

        List<Record> records = recordRepository.findAllByCourse_IdOrderByTimeDesc(courseId);
        
        if (records.isEmpty()) {
            throw new CustomException(RecordErrorCode.NO_RECORDS_FOUND);
        }

        return records.stream()
                .map(record -> new RecordDTO(
                        record.getId(),
                        record.getCourse().getId(),
                        record.getTime(),
                        record.getKcal(),
                        record.getWalkCnt()
                ))
                .collect(Collectors.toList());
    }
}
