package com.runtracker.domain.course.controller;

import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseCreateDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.dto.FinishRunning;
import com.runtracker.domain.course.service.CourseService;
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
        courseService.saveCourse(userDetails.getMemberId(), request);
        return ApiResponse.ok();
    }

    @GetMapping("/nearby")
    public ApiResponse<List<Response>> getNearbyCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Integer limit) {
        List<Response> courses = courseService.getNearbyCourses(
                userDetails.getMemberId(), latitude, longitude, limit);
        return ApiResponse.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ApiResponse<CourseDetailDTO> getCourseDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        CourseDetailDTO courseDetail = courseService.getCourseDetail(courseId);
        return ApiResponse.ok(courseDetail);
    }

    @PostMapping("/{courseId}/running")
    public ApiResponse<Void> startRunningWithCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        courseService.startRunningCourse(userDetails.getMemberId(), courseId);
        return ApiResponse.ok();
    }

    @PostMapping("/finish")
    public ApiResponse<Void> finishRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FinishRunning request) {
        courseService.finishRunning(userDetails.getMemberId(), request);
        return ApiResponse.ok();
    }

    @PostMapping("/test/save")
    public ApiResponse<Void> saveTestCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CourseCreateDTO request) {
        courseService.saveTestCourse(userDetails.getMemberId(), request);
        return ApiResponse.ok();
    }

    @GetMapping("/recommend/record")
    public ApiResponse<List<CourseDetailDTO>> getRecommendedCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<CourseDetailDTO> recommendedCourses = courseService.getRecommendedCourses(
                userDetails.getMemberId(), latitude, longitude);
        return ApiResponse.ok(recommendedCourses);
    }

    @GetMapping("/recommend/setting")
    public ApiResponse<List<CourseDetailDTO>> getRecommendedCoursesBySetting(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<CourseDetailDTO> recommendedCourses = courseService.getRecommendedCoursesBySetting(
                userDetails.getMemberId(), latitude, longitude);
        return ApiResponse.ok(recommendedCourses);
    }
}
