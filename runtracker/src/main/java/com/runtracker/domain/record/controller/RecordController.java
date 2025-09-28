package com.runtracker.domain.record.controller;

import com.runtracker.domain.record.dto.RunningRecordDTO;
import com.runtracker.domain.record.service.RecordService;
import com.runtracker.global.code.DateConstants;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {
    
    private final RecordService recordService;

    @GetMapping("/summary")
    public ApiResponse<List<RunningRecordDTO>> getRunningRecordsSummary(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) String type,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate endDate) {
        List<RunningRecordDTO> records = recordService.getRunningRecordsSummary(userDetails.getMemberId(), type, date, endDate);
        return ApiResponse.ok(records);
    }

    @GetMapping("/user")
    public ApiResponse<List<RunningRecordDTO>> getAllRunningRecords(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<RunningRecordDTO> records = recordService.getAllRunningRecords(userDetails.getMemberId());
        return ApiResponse.ok(records);
    }

    @GetMapping("/{recordId}")
    public ApiResponse<RunningRecordDTO> getRunningRecord(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long recordId) {
        RunningRecordDTO record = recordService.getRunningRecordById(userDetails.getMemberId(), recordId);
        return ApiResponse.ok(record);
    }
}