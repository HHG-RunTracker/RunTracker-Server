package com.runtracker_prototype.controller;

import com.runtracker_prototype.code.CommonResponseCode;
import com.runtracker_prototype.dto.RecordDTO;
import com.runtracker_prototype.response.ApiResponse;
import com.runtracker_prototype.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/proto/records")
@RequiredArgsConstructor
@Tag(name = "Record API", description = "기록 관련 API")
public class RecordController {

    private final RecordService recordService;
    
    @Operation(summary = "전체 기록 불러오기", description = "전체 기록을 시간순으로 반환")
    @GetMapping
    public ApiResponse<List<RecordDTO>> getRecords() {
        try {
            return ApiResponse.ok(new ArrayList<>());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "코스별 기록 불러오기", description = "코스별 기록을 시간순으로 반환")
    @GetMapping("/{courseId}")
    public ApiResponse<List<RecordDTO>> getRecordsByCourseId(@PathVariable Long courseId) {
        try {
            return ApiResponse.ok(new ArrayList<>());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
