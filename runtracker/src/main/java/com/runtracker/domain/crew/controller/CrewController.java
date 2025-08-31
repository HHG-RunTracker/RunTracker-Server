package com.runtracker.domain.crew.controller;

import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCourseRecommendationDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.crew.dto.CrewRunningFinishDTO;
import com.runtracker.domain.crew.dto.CrewRecordDTO;
import com.runtracker.domain.crew.dto.CrewRunningDTO;
import com.runtracker.domain.crew.dto.CrewDetailDTO;
import com.runtracker.domain.crew.dto.CrewListDTO;
import com.runtracker.domain.crew.dto.CrewManagementDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.dto.MemberProfileDTO;
import com.runtracker.domain.crew.dto.CrewRankingDTO;
import com.runtracker.domain.crew.dto.CrewMemberRankingDTO;
import com.runtracker.domain.crew.service.CrewService;
import com.runtracker.domain.crew.service.CrewRunningService;
import com.runtracker.domain.crew.service.CrewRankingService;
import com.runtracker.domain.crew.service.CrewMemberRankingService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crew")
public class CrewController {

    private final CrewService crewService;
    private final CrewRunningService crewRunningService;
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

    @GetMapping("/{crewId}/recommended-courses")
    public ApiResponse<List<CrewCourseRecommendationDTO.Response>> getRecommendedCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Double minDistance,
            @RequestParam(required = false) Double maxDistance) {

        List<CrewCourseRecommendationDTO.Response> response = 
                crewRunningService.getRecommendedCourses(crewId, region, minDistance, maxDistance, userDetails);

        return ApiResponse.ok(response);
    }

    @PostMapping("/{crewId}/running/create")
    public ApiResponse<Void> createCrewRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @RequestBody CrewRunningDTO.CreateRequest request) {
        
        crewRunningService.createCrewRunning(crewId, request, userDetails);
        
        return ApiResponse.ok();
    }

    @GetMapping("/{crewId}/running/list")
    public ApiResponse<List<CrewRunningDTO.Response>> getCrewRunnings(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        List<CrewRunningDTO.Response> response = crewRunningService.getCrewRunnings(crewId, userDetails);
        
        return ApiResponse.ok(response);
    }

    @PostMapping("/{crewId}/running/{crewRunningId}/join")
    public ApiResponse<Void> joinCrewRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId) {
        
        crewRunningService.joinCrewRunning(crewId, crewRunningId, userDetails);
        
        return ApiResponse.ok();
    }

    @PostMapping("/{crewId}/running/{crewRunningId}/leave")
    public ApiResponse<Void> leaveCrewRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId) {
        
        crewRunningService.leaveCrewRunning(crewId, crewRunningId, userDetails);
        
        return ApiResponse.ok();
    }

    @GetMapping("/{crewId}/running/list/{crewRunningId}")
    public ApiResponse<CrewRunningDTO.Response> getCrewRunningDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId) {
        
        CrewRunningDTO.Response response = crewRunningService.getCrewRunningDetail(crewId, crewRunningId, userDetails);
        
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{crewId}/running/{crewRunningId}")
    public ApiResponse<Void> deleteCrewRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId) {
        
        crewRunningService.deleteCrewRunning(crewId, crewRunningId, userDetails);
        
        return ApiResponse.ok();
    }

    @PostMapping("/{crewId}/running/{crewRunningId}/course/{courseId}")
    public ApiResponse<CourseDetailDTO> startCrewRunningWithCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId,
            @PathVariable Long courseId) {
        
        CrewRunningDTO.StartRunningWithCourseRequest request = CrewRunningDTO.StartRunningWithCourseRequest.builder()
                .courseId(courseId)
                .build();
        CourseDetailDTO courseDetail = crewRunningService.startCrewRunningWithCourse(crewId, crewRunningId, request, userDetails);
        
        return ApiResponse.ok(courseDetail);
    }

    @PostMapping("/{crewId}/running/{crewRunningId}/free-running")
    public ApiResponse<Void> startCrewFreeRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId,
            @RequestBody CourseDTO courseDTO) {
        
        crewRunningService.startCrewFreeRunning(crewId, crewRunningId, courseDTO, userDetails);
        
        return ApiResponse.ok();
    }

    @PostMapping("/{crewId}/running/{crewRunningId}/finish")
    public ApiResponse<Void> finishCrewRunning(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId,
            @RequestBody CrewRunningFinishDTO finishDTO) {
        
        crewRunningService.finishCrewRunning(crewId, crewRunningId, finishDTO, userDetails);
        
        return ApiResponse.ok();
    }

    @GetMapping("/{crewId}/records")
    public ApiResponse<List<CrewRecordDTO>> getCrewRecords(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId) {
        
        List<CrewRecordDTO> response = crewService.getCrewRecords(crewId, userDetails);
        
        return ApiResponse.ok(response);
    }

    @GetMapping("/{crewId}/records/{crewRunningId}")
    public ApiResponse<CrewRecordDTO> getCrewRecord(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long crewId,
            @PathVariable Long crewRunningId) {
        
        CrewRecordDTO response = crewService.getCrewRecord(crewId, crewRunningId, userDetails);
        
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