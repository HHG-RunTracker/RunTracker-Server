package com.runtracker.domain.record.controller;

import com.runtracker.domain.record.dto.RecordDetailDTO;
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

    @GetMapping("/{courseId}")
    public ApiResponse<RecordDetailDTO> getCourseDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        try {
            RecordDetailDTO courseDetail = recordService.getCourseDetail(courseId);
            return ApiResponse.ok(courseDetail);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date")
    public ApiResponse<List<RecordDetailDTO>> getCoursesByDateRange(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate startDate,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate endDate) {
        try {
            List<RecordDetailDTO> courses = recordService.getCoursesByDate(userDetails.getMemberId(), startDate, endDate);
            return ApiResponse.ok(courses);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/week")
    public ApiResponse<List<RecordDetailDTO>> getCoursesByWeek(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate weekDate) {
        try {
            List<RecordDetailDTO> courses = recordService.getCoursesByWeek(userDetails.getMemberId(), weekDate);
            return ApiResponse.ok(courses);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/month")
    public ApiResponse<List<RecordDetailDTO>> getCoursesByMonth(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = true) @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDate monthDate) {
        try {
            List<RecordDetailDTO> courses = recordService.getCoursesByMonth(userDetails.getMemberId(), monthDate);
            return ApiResponse.ok(courses);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}