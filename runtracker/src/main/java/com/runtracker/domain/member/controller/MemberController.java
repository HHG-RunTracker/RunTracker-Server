package com.runtracker.domain.member.controller;

import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.domain.member.service.MemberService;
import com.runtracker.domain.member.service.AuthService;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.dto.MemberUpdateDTO;
import com.runtracker.domain.member.dto.NotificationSettingDTO;
import com.runtracker.domain.member.dto.RunningBackupDTO;
import com.runtracker.domain.member.dto.FcmTokenDTO;
import com.runtracker.domain.member.dto.RunningSettingDTO;
import com.runtracker.domain.member.dto.MemberProfileDTO;
import com.runtracker.global.jwt.dto.TokenDataDto;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping("/refresh")
    public ApiResponse<TokenDataDto> refreshToken(@Valid @RequestBody LoginTokenDto.RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refreshToken(request.getRefreshToken()));
    }

    /**
     * 이름으로 소셜 ID 검색
     */
    @GetMapping("/search-name")
    public ApiResponse<LoginTokenDto.MemberSearchResult> findSocialIdByName(@RequestParam("name") String name) {
        return ApiResponse.ok(memberService.findMemberByName(name));
    }

    /**
     * 테스트용: 소셜 ID로 사용자 검색 후 토큰 발급
     */
    @PostMapping("/test-login")
    public ApiResponse<LoginTokenDto> testLogin(@Valid @RequestBody LoginTokenDto.SocialIdLoginRequest request) {
        return ApiResponse.ok(authService.testLoginBySocialId(request.getSocialId(), request.getKey()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.logout(userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @DeleteMapping("/withdrawal")
    public ApiResponse<Void> withdrawMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.withdrawMember(userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @GetMapping("/profile")
    public ApiResponse<MemberProfileDTO.Response> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberService.getMemberById(userDetails.getMemberId());
        return ApiResponse.ok(MemberProfileDTO.Response.from(member));
    }

    @PatchMapping("/update")
    public ApiResponse<Void> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @Valid @RequestBody MemberUpdateDTO.Request request) {
        memberService.updateProfile(userDetails.getMemberId(), request);
        return ApiResponse.ok();
    }

    @PatchMapping("/notification")
    public ApiResponse<Void> updateNotificationSetting(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @Valid @RequestBody NotificationSettingDTO.Request request) {
        memberService.updateNotificationSetting(userDetails.getMemberId(), request);
        return ApiResponse.ok();
    }

    @GetMapping("/running-setting")
    public ApiResponse<RunningSettingDTO.Response> getRunningSetting(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        RunningSettingDTO.Response response = memberService.getRunningSetting(userDetails.getMemberId());
        return ApiResponse.ok(response);
    }

    @PatchMapping("/running-setting")
    public ApiResponse<Void> updateRunningSetting(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @Valid @RequestBody RunningSettingDTO.Request request) {
        memberService.updateRunningSetting(userDetails.getMemberId(), request);
        return ApiResponse.ok();
    }

    @PostMapping("/backup")
    public ApiResponse<Void> createOrUpdateBackup(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.createBackup(userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @PostMapping("/restore")
    public ApiResponse<Void> restoreRunningRecords(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.restoreRunningRecords(userDetails.getMemberId());
        return ApiResponse.ok();
    }

    @GetMapping("/backup/info")
    public ApiResponse<RunningBackupDTO.BackupInfo> getBackupInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        RunningBackupDTO.BackupInfo backupInfo = memberService.getBackupInfo(userDetails.getMemberId());
        return ApiResponse.ok(backupInfo);
    }

    @PostMapping("/fcm-token")
    public ApiResponse<Void> registerFcmToken(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @Valid @RequestBody FcmTokenDTO.Request fcmTokenDTO) {
        memberService.updateFcmToken(userDetails.getMemberId(), fcmTokenDTO.getFcmToken());
        return ApiResponse.ok();
    }

    @PostMapping("/fcm-token/remove")
    public ApiResponse<Void> removeFcmToken(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.removeFcmToken(userDetails.getMemberId());
        return ApiResponse.ok();
    }

}
