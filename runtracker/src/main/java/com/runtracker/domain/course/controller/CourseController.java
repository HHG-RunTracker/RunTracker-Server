package com.runtracker.domain.course.controller;

import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.service.CourseService;
import com.runtracker.global.code.CommonResponseCode;
import com.runtracker.global.exception.CustomException;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/free-running")
    public ApiResponse<Void> createFreeRunningCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CourseDTO courseDTO) {
        try {
            courseDTO.setMemberId(userDetails.getMemberId());
            courseService.createFreeRunningCourse(courseDTO);
            return ApiResponse.ok();
        } catch (CustomException e) {
            return ApiResponse.error(e.getResponseCode());
        } catch (Exception e) {
            return ApiResponse.error(CommonResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
