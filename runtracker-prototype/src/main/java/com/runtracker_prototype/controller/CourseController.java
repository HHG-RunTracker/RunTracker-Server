package com.runtracker_prototype.controller;

import com.runtracker_prototype.dto.CourseDTO;
import com.runtracker_prototype.exception.CustomException;
import com.runtracker_prototype.response.ApiResponse;
import com.runtracker_prototype.service.CourseService;
import com.runtracker_prototype.service.S3Service;
import com.runtracker_prototype.code.CommonResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/proto/courses")
@RequiredArgsConstructor
@Tag(name = "Course API", description = "코스 관련 API")
public class CourseController {

    private final CourseService courseService;
    private final S3Service s3Service;

    @Operation(summary = "반경 내 주변 코스들 검색", description = "현재 위치를 기준으로 반경 내 코스들의 시작점 좌표 리스트를 반환")
    @GetMapping("/nearby")
    public ApiResponse<List<CourseDTO>> getNearbyCourses() {
        try {
            List<CourseDTO> courses = new ArrayList<>();
            return ApiResponse.ok(courses);
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "자유 러닝 코스 생성", description = "자유 러닝을 기반으로 코스를 생성")
    @PostMapping("/custom")
    public ApiResponse<CourseDTO> addCustomCourse(@RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO savedCourse = courseService.createCustomCourse(courseDTO);
            return ApiResponse.ok(savedCourse);
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "코스 상세 보기", description = "코스의 세부 사항을 반환")
    @GetMapping("/{courseId}")
    public ApiResponse<CourseDTO> getCourseById(@PathVariable Long courseId) {
        try {
            CourseDTO courseDTO = new CourseDTO();
            return ApiResponse.ok(courseDTO);
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}