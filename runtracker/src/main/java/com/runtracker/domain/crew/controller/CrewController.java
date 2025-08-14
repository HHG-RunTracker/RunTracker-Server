package com.runtracker.domain.crew.controller;

import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.dto.CrewDetailDTO;
import com.runtracker.domain.crew.dto.CrewListDTO;
import com.runtracker.domain.crew.dto.CrewManagementDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.dto.MemberProfileDTO;
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CrewCreateDTO.Request request) {
        
        Long leaderId = userDetails.getMemberId();
        crewService.createCrew(request, leaderId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/join/{crewId}")
    public ApiResponse<Void> applyToJoinCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long memberId = userDetails.getMemberId();
        crewService.applyToJoinCrew(crewId, memberId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/join/cancel/{crewId}")
    public ApiResponse<Void> cancelCrewApplication(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long memberId = userDetails.getMemberId();
        crewService.cancelCrewApplication(crewId, memberId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/approval/{crewId}")
    public ApiResponse<Void> processJoinRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewApprovalDTO.Request request) {
        
        Long leaderId = userDetails.getMemberId();
        crewService.processJoinRequest(crewId, request, leaderId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/member/role/{crewId}")
    public ApiResponse<Void> updateCrewMemberRole(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewMemberUpdateDTO.Request request) {
        
        Long managerId = userDetails.getMemberId();
        crewService.updateCrewMemberRole(crewId, request, managerId);
        
        return ApiResponse.ok();
    }
    
    @PatchMapping("/update/{crewId}")
    public ApiResponse<Void> updateCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewUpdateDTO.Request request) {
        
        Long leaderId = userDetails.getMemberId();
        crewService.updateCrew(crewId, request, leaderId);
        
        return ApiResponse.ok();
    }
    
    @DeleteMapping("/delete/{crewId}")
    public ApiResponse<Void> deleteCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long leaderId = userDetails.getMemberId();
        crewService.deleteCrew(crewId, leaderId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/ban/{crewId}")
    public ApiResponse<Void> banCrewMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestParam Long memberId) {
        
        Long managerId = userDetails.getMemberId();
        crewService.banCrewMember(crewId, memberId, managerId);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/leave/{crewId}")
    public ApiResponse<Void> leaveCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long memberId = userDetails.getMemberId();
        crewService.leaveCrew(crewId, memberId);
        
        return ApiResponse.ok();
    }
    
    @GetMapping("/list")
    public ApiResponse<CrewListDTO.ListResponse> getCrewList(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Long memberId = userDetails.getMemberId();
        CrewListDTO.ListResponse response = crewService.getAllCrews();
        return ApiResponse.ok(response);
    }
    
    @GetMapping("/{crewId}")
    public ApiResponse<CrewDetailDTO.Response> getCrewDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long memberId = userDetails.getMemberId();
        CrewDetailDTO.Response response = crewService.getCrewDetail(crewId);
        return ApiResponse.ok(response);
    }
    
    @GetMapping("/list/pending/{crewId}")
    public ApiResponse<CrewManagementDTO.PendingMembersResponse> getPendingMembers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long managerId = userDetails.getMemberId();
        CrewManagementDTO.PendingMembersResponse response = crewService.getPendingMembers(crewId, managerId);
        
        return ApiResponse.ok(response);
    }
    
    @GetMapping("/list/banned/{crewId}")
    public ApiResponse<CrewManagementDTO.BannedMembersResponse> getBannedMembers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        Long managerId = userDetails.getMemberId();
        CrewManagementDTO.BannedMembersResponse response = crewService.getBannedMembers(crewId, managerId);
        
        return ApiResponse.ok(response);
    }
    
    @GetMapping("/member/{memberId}")
    public ApiResponse<MemberProfileDTO> getMemberProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long memberId) {
        
        Long requesterId = userDetails.getMemberId();
        MemberProfileDTO response = crewService.getMemberProfile(memberId, requesterId);
        
        return ApiResponse.ok(response);
    }
}