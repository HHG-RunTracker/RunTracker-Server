package com.runtracker.domain.crew.controller;

import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.dto.CrewDetailDTO;
import com.runtracker.domain.crew.dto.CrewListDTO;
import com.runtracker.domain.crew.dto.CrewManagementDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.dto.MemberProfileDTO;
import com.runtracker.domain.crew.dto.CrewRankingDTO;
import com.runtracker.domain.crew.dto.CrewMemberRankingDTO;
import com.runtracker.domain.crew.service.CrewService;
import com.runtracker.domain.crew.service.CrewRankingService;
import com.runtracker.domain.crew.service.CrewMemberRankingService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crew")
public class CrewController {

    private final CrewService crewService;
    private final CrewRankingService crewRankingService;
    private final CrewMemberRankingService crewMemberRankingService;

    @PostMapping("/create")
    public ApiResponse<Void> createCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CrewCreateDTO.Request request) {
        
        crewService.createCrew(request, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/join/{crewId}")
    public ApiResponse<Void> applyToJoinCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        crewService.applyToJoinCrew(crewId, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/join/cancel/{crewId}")
    public ApiResponse<Void> cancelCrewApplication(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        crewService.cancelCrewApplication(crewId, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/approval/{crewId}")
    public ApiResponse<Void> processJoinRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewApprovalDTO.Request request) {
        
        crewService.processJoinRequest(crewId, request, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/member/role/{crewId}")
    public ApiResponse<Void> updateCrewMemberRole(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewMemberUpdateDTO.Request request) {
        
        crewService.updateCrewMemberRole(crewId, request, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PatchMapping("/update/{crewId}")
    public ApiResponse<Void> updateCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewUpdateDTO.Request request) {
        
        crewService.updateCrew(crewId, request, userDetails);
        
        return ApiResponse.ok();
    }
    
    @DeleteMapping("/delete/{crewId}")
    public ApiResponse<Void> deleteCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        crewService.deleteCrew(crewId, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/ban/{crewId}")
    public ApiResponse<Void> banCrewMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestParam Long memberId) {
        
        crewService.banCrewMember(crewId, memberId, userDetails);
        
        return ApiResponse.ok();
    }
    
    @PostMapping("/leave/{crewId}")
    public ApiResponse<Void> leaveCrew(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        crewService.leaveCrew(crewId, userDetails);
        
        return ApiResponse.ok();
    }
    
    @GetMapping("/list")
    public ApiResponse<CrewListDTO.ListResponse> getCrewList(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMemberId();
        CrewListDTO.ListResponse response = crewService.getAllCrews();
        return ApiResponse.ok(response);
    }

    @GetMapping("/search")
    public ApiResponse<CrewListDTO.ListResponse> searchCrewsByName(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String name) {

        CrewListDTO.ListResponse response = crewService.searchCrewsByName(name);
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
        
        CrewManagementDTO.PendingMembersResponse response = crewService.getPendingMembers(crewId, userDetails);
        
        return ApiResponse.ok(response);
    }
    
    @GetMapping("/list/banned/{crewId}")
    public ApiResponse<CrewManagementDTO.BannedMembersResponse> getBannedMembers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        CrewManagementDTO.BannedMembersResponse response = crewService.getBannedMembers(crewId, userDetails);
        
        return ApiResponse.ok(response);
    }
    
    @GetMapping("/member/{memberId}")
    public ApiResponse<MemberProfileDTO> getMemberProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long memberId) {
        
        MemberProfileDTO response = crewService.getMemberProfile(memberId, userDetails);
        
        return ApiResponse.ok(response);
    }

    @GetMapping("/ranking/list")
    public ApiResponse<CrewRankingDTO.Response> getCurrentRanking(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocalDate today = LocalDate.now();
        CrewRankingDTO.Response response = crewRankingService.getDailyRanking(today);
        
        return ApiResponse.ok(response);
    }

    @PostMapping("/ranking/recalculate")
    public ApiResponse<Void> recalculateRanking(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        LocalDate today = LocalDate.now();
        crewRankingService.recalculateRanking(today);
        
        return ApiResponse.ok();
    }

    @GetMapping("/{crewId}/member-ranking")
    public ApiResponse<CrewMemberRankingDTO.Response> getCrewMemberRanking(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        LocalDate today = LocalDate.now();
        CrewMemberRankingDTO.Response response = crewMemberRankingService.getCrewMemberRanking(crewId, today);
        
        return ApiResponse.ok(response);
    }

    @PostMapping("/{crewId}/member-ranking/recalculate")
    public ApiResponse<Void> recalculateCrewMemberRanking(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        LocalDate today = LocalDate.now();
        crewMemberRankingService.recalculateCrewMemberRanking(crewId, today);
        
        return ApiResponse.ok();
    }
}