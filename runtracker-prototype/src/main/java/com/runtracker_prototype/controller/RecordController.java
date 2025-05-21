package com.runtracker_prototype.controller;

import com.runtracker_prototype.code.CommonResponseCode;
import com.runtracker_prototype.dto.RecordDTO;
import com.runtracker_prototype.exception.CustomException;
import com.runtracker_prototype.response.ApiResponse;
import com.runtracker_prototype.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proto/records")
@RequiredArgsConstructor
@Tag(name = "Record API", description = "기록 관련 API")
public class RecordController {

    private final RecordService recordService;
    
    @Operation(summary = "러닝 기록 저장", description = "러닝이 끝난 후 기록을 저장 (시간은 자동으로 현재 시간으로 저장됨)")
    @PostMapping
    public ApiResponse<RecordDTO> saveRecord(@RequestBody RecordDTO recordDTO) {
        try {
            RecordDTO savedRecord = recordService.saveRecord(recordDTO);
            return ApiResponse.ok(savedRecord);
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "전체 기록 불러오기", description = "전체 기록을 시간순으로 반환")
    @GetMapping
    public ApiResponse<List<RecordDTO>> getRecords() {
        try {
            List<RecordDTO> records = recordService.getAllRecords();
            return ApiResponse.ok(records);
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "코스별 기록 불러오기", description = "코스별 기록을 시간순으로 반환")
    @GetMapping("/{courseId}")
    public ApiResponse<List<RecordDTO>> getRecordsByCourseId(@PathVariable("courseId") Long courseId) {
        try {
            List<RecordDTO> records = recordService.getRecordsByCourseId(courseId);
            return ApiResponse.ok(records);
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
