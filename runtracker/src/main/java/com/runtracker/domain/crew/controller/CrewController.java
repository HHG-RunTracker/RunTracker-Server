package com.runtracker.domain.crew.controller;

import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.service.CrewService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crew")
public class CrewController {

    private final CrewService crewService;

    @PostMapping("/create")
    public ApiResponse<Void> createCrew(
            @RequestBody CrewCreateDTO.Request request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Long leaderId = userDetails.getMemberId();
        crewService.createCrew(request, leaderId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/join/{crewId}")
    public ApiResponse<Void> applyToJoinCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long applicantId = userDetails.getMemberId();
        crewService.applyToJoinCrew(crewId, applicantId);
        
        return ApiResponse.ok();
    }
}