package com.runtracker.domain.member.controller;

import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.domain.member.service.MemberService;
import com.runtracker.domain.member.service.AuthService;
import com.runtracker.domain.member.entity.Member;
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
    public ApiResponse<Member> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberService.getMemberById(userDetails.getMemberId());
        return ApiResponse.ok(member);
    }
}
