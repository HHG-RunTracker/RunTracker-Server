package com.runtracker.domain.record.controller;

import com.runtracker.domain.record.dto.RunningRecordDTO;
import com.runtracker.domain.record.service.RecordService;
import com.runtracker.global.code.CommonResponseCode;
import com.runtracker.global.code.DateConstants;
import com.runtracker.global.exception.CustomException;
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

    @PostMapping("/save")
    public ApiResponse<Void> saveRunningRecord(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody RunningRecordDTO createDTO) {
        try {
            recordService.saveRunningRecord(userDetails.getMemberId(), createDTO);
            return ApiResponse.ok();
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date")
    public ApiResponse<List<RunningRecordDTO>> getRunningRecordsByDateRange(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate startDate,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate endDate) {
        try {
            List<RunningRecordDTO> records = recordService.getRunningRecordsByDate(userDetails.getMemberId(), startDate, endDate);
            return ApiResponse.ok(records);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/week")
    public ApiResponse<List<RunningRecordDTO>> getRunningRecordsByWeek(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate weekDate) {
        try {
            List<RunningRecordDTO> records = recordService.getRunningRecordsByWeek(userDetails.getMemberId(), weekDate);
            return ApiResponse.ok(records);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/month")
    public ApiResponse<List<RunningRecordDTO>> getRunningRecordsByMonth(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate monthDate) {
        try {
            List<RunningRecordDTO> records = recordService.getRunningRecordsByMonth(userDetails.getMemberId(), monthDate);
            return ApiResponse.ok(records);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ApiResponse<List<RunningRecordDTO>> getAllRunningRecords(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            List<RunningRecordDTO> records = recordService.getAllRunningRecords(userDetails.getMemberId());
            return ApiResponse.ok(records);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{recordId}")
    public ApiResponse<RunningRecordDTO> getRunningRecord(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long recordId) {
        try {
            RunningRecordDTO record = recordService.getRunningRecordById(userDetails.getMemberId(), recordId);
            return ApiResponse.ok(record);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}