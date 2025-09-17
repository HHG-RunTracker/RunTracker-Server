package com.runtracker.domain.course.controller;

import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseCreateDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Request;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.dto.FinishRunning;
import com.runtracker.domain.course.service.CourseService;
import com.runtracker.global.code.CommonResponseCode;
import com.runtracker.global.exception.CustomException;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/save")
    public ApiResponse<Void> saveCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CourseCreateDTO request) {
        try {
            courseService.saveCourse(userDetails.getMemberId(), request);
            return ApiResponse.ok();
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nearby")
    public ApiResponse<List<Response>> getNearbyCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) Integer limit) {
        try {
            Request request = Request.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .radius(radius)
                    .limit(limit)
                    .build();
            
            List<Response> courses = courseService.getNearbyCourses(request);
            return ApiResponse.ok(courses);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{courseId}")
    public ApiResponse<CourseDetailDTO> getCourseDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        try {
            CourseDetailDTO courseDetail = courseService.getCourseDetail(courseId);
            return ApiResponse.ok(courseDetail);
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/running")
    public ApiResponse<Void> startRunningWithCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        try {
            courseService.startRunningCourse(userDetails.getMemberId(), courseId);
            return ApiResponse.ok();
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/finish")
    public ApiResponse<Void> finishRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FinishRunning request) {
        try {
            courseService.finishRunning(userDetails.getMemberId(), request);
            return ApiResponse.ok();
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
