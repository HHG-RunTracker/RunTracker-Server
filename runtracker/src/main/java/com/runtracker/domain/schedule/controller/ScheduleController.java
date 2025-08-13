package com.runtracker.domain.schedule.controller;

import com.runtracker.domain.schedule.dto.ScheduleCreateDTO;
import com.runtracker.domain.schedule.dto.ScheduleDetailDTO;
import com.runtracker.domain.schedule.dto.ScheduleListDTO;
import com.runtracker.domain.schedule.dto.ScheduleUpdateDTO;
import com.runtracker.domain.schedule.service.ScheduleService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
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

    @GetMapping("/list")
    public ApiResponse<ScheduleListDTO.ListResponse> getCrewSchedules(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ScheduleListDTO.ListResponse response = scheduleService.getCrewSchedulesByMemberId(userDetails.getMemberId());
        return ApiResponse.ok(response);
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<ScheduleDetailDTO.Response> getScheduleDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long scheduleId) {
        ScheduleDetailDTO.Response response = scheduleService.getScheduleDetail(scheduleId, userDetails.getMemberId());
        return ApiResponse.ok(response);
    }

    @PatchMapping("/update/{scheduleId}")
    public ApiResponse<Void> updateSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateDTO scheduleUpdateDTO) {
        scheduleService.updateSchedule(scheduleId, scheduleUpdateDTO, userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @DeleteMapping("/delete/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId, userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @PostMapping("/join/{scheduleId}")
    public ApiResponse<Void> joinSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long scheduleId) {
        scheduleService.joinSchedule(scheduleId, userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @PostMapping("/cancel/{scheduleId}")
    public ApiResponse<Void> cancelSchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long scheduleId) {
        scheduleService.cancelSchedule(scheduleId, userDetails.getMemberId());
        return ApiResponse.ok();
    }
}