package com.runtracker.domain.schedule.controller;

import com.runtracker.domain.schedule.dto.ScheduleCreateDTO;
import com.runtracker.domain.schedule.service.ScheduleService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/create")
    public ApiResponse<Void> createSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ScheduleCreateDTO scheduleCreateDTO) {
        scheduleService.createSchedule(scheduleCreateDTO, userDetails.getMemberId());
        return ApiResponse.ok();
    }
}