package com.runtracker_prototype.service;

import com.runtracker_prototype.domain.Record;
import com.runtracker_prototype.dto.RecordDTO;
import com.runtracker_prototype.exception.NoRecordsFoundException;
import com.runtracker_prototype.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {
    
    private final RecordRepository recordRepository;

    public List<RecordDTO> getAllRecords() {
        List<Record> records = recordRepository.findAllByOrderByTimeDesc();
        
        if (records.isEmpty()) {
            throw new NoRecordsFoundException();
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
