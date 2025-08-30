package com.runtracker.domain.member.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.dto.MemberUpdateDTO;
import com.runtracker.domain.member.dto.NotificationSettingDTO;
import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.global.jwt.dto.TokenDataDto;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends RunTrackerDocumentApiTester {

    @Test
    void refreshTokenTest() throws Exception {
        // given
        TokenDataDto tokenData = TokenDataDto.builder()
                .grantType("Bearer")
                .accessToken("new_access_token_value")
                .refreshToken("new_refresh_token_value")
                .accessTokenExpiredAt(System.currentTimeMillis() + 3600000)
                .refreshTokenExpiredAt(System.currentTimeMillis() + 86400000)
                .build();

        given(authService.refreshToken(anyString())).willReturn(tokenData);

        // when
        this.mockMvc.perform(post("/api/members/refresh")
                        .contentType("application/json")
                        .content(toJson(Map.of("refreshToken", "refresh_token_example"))))
                .andExpect(status().isOk())
                .andDo(document("member-refresh-token",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .description("리프레시 토큰으로 액세스 토큰 갱신")
                                        .requestFields(
                                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("갱신할 리프레시 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.grantType").type(JsonFieldType.STRING).description("토큰 타입 (Bearer)"),
                                                fieldWithPath("body.accessToken").type(JsonFieldType.STRING).description("새로 발급된 액세스 토큰"),
                                                fieldWithPath("body.refreshToken").type(JsonFieldType.STRING).description("새로 발급된 리프레시 토큰"),
                                                fieldWithPath("body.accessTokenExpiredAt").type(JsonFieldType.NUMBER).description("액세스 토큰 만료 시간 (timestamp)"),
                                                fieldWithPath("body.refreshTokenExpiredAt").type(JsonFieldType.NUMBER).description("리프레시 토큰 만료 시간 (timestamp)")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void findByNameTest() throws Exception {
        // given
        LoginTokenDto.MemberSearchResult searchResult = LoginTokenDto.MemberSearchResult.builder()
                .userId(1L)
                .socialId("kakao_12345")
                .build();

        given(memberService.findMemberByName(anyString())).willReturn(searchResult);

        // when
        this.mockMvc.perform(get("/api/members/search-name")
                        .queryParam("name", "홍길동"))
                .andExpect(status().isOk())
                .andDo(document("member-search-by-name",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .description("이름으로 소셜 ID 검색")
                                        .queryParameters(
                                                parameterWithName("name").description("검색할 회원 이름")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),  
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                                fieldWithPath("body.socialId").type(JsonFieldType.STRING).description("소셜 로그인 ID")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void testLoginTest() throws Exception {
        // given  
        TokenDataDto tokenData = TokenDataDto.builder()
                .grantType("Bearer")
                .accessToken("access_token_example")
                .refreshToken("refresh_token_example")
                .accessTokenExpiredAt(System.currentTimeMillis() + 3600000)
                .refreshTokenExpiredAt(System.currentTimeMillis() + 86400000)
                .build();

        LoginTokenDto loginResponse = LoginTokenDto.builder()
                .userId(1L)
                .socialId("kakao_12345")
                .tokenData(tokenData)
                .build();

        given(authService.testLoginBySocialId(anyString(), anyString())).willReturn(loginResponse);

        // when
        this.mockMvc.perform(post("/api/members/test-login")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "socialId", "kakao_12345",
                                "key", "a9F3kLmP7wQzX1bC"
                        ))))
                .andExpect(status().isOk())
                .andDo(document("member-test-login",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .description("소셜 ID로 토큰 발급 (테스트용)")
                                        .requestFields(
                                                fieldWithPath("socialId").type(JsonFieldType.STRING).description("소셜 로그인 ID"),
                                                fieldWithPath("key").type(JsonFieldType.STRING).description("사용자 인증 키")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),  
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                                fieldWithPath("body.socialId").type(JsonFieldType.STRING).description("소셜 로그인 ID"),
                                                fieldWithPath("body.tokenData").type(JsonFieldType.OBJECT).description("토큰 정보"),
                                                fieldWithPath("body.tokenData.grantType").type(JsonFieldType.STRING).description("토큰 타입 (Bearer)"),
                                                fieldWithPath("body.tokenData.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                                fieldWithPath("body.tokenData.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                                fieldWithPath("body.tokenData.accessTokenExpiredAt").type(JsonFieldType.NUMBER).description("액세스 토큰 만료 시간 (timestamp)"),
                                                fieldWithPath("body.tokenData.refreshTokenExpiredAt").type(JsonFieldType.NUMBER).description("리프레시 토큰 만료 시간 (timestamp)")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void logoutTest() throws Exception {
        // given
        doNothing().when(memberService).logout(anyLong());

        // when & then
        this.mockMvc.perform(post("/api/members/logout")
                        .header("Authorization", "Bearer access_token_example"))
                .andExpect(status().isOk())
                .andDo(document("member-logout",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .description("로그아웃")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("Bearer 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.NULL).description("응답 본문 (null)").optional()
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getProfileTest() throws Exception {
        // given
        Member member = Member.builder()
                .socialAttr("kakao")
                .socialId("kakao_12345")
                .photo("https://example.com/photo.jpg")
                .name("홍길동")
                .introduce("안녕하세요! 러닝을 좋아하는 홍길동입니다.")
                .age(25)
                .gender(true)
                .region("서울")
                .difficulty("EASY")
                .temperature(36.5)
                .point(100)
                .searchBlock(false)
                .profileBlock(false)
                .notifyBlock(true)
                .build();

        given(memberService.getMemberById(anyLong())).willReturn(member);

        // when & then
        this.mockMvc.perform(get("/api/members/profile")
                        .header("Authorization", "Bearer access_token_example"))
                .andExpect(status().isOk())
                .andDo(document("member-get-profile",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .description("로그인한 사용자 프로필 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("회원 ID").optional(),
                                                fieldWithPath("body.socialAttr").type(JsonFieldType.STRING).description("소셜 로그인 제공자").optional(),
                                                fieldWithPath("body.socialId").type(JsonFieldType.STRING).description("소셜 로그인 ID"),
                                                fieldWithPath("body.photo").type(JsonFieldType.STRING).description("프로필 사진 URL").optional(),
                                                fieldWithPath("body.name").type(JsonFieldType.STRING).description("이름").optional(),
                                                fieldWithPath("body.introduce").type(JsonFieldType.STRING).description("자기소개").optional(),
                                                fieldWithPath("body.age").type(JsonFieldType.NUMBER).description("나이").optional(),
                                                fieldWithPath("body.gender").type(JsonFieldType.BOOLEAN).description("성별 (true: 남성, false: 여성)").optional(),
                                                fieldWithPath("body.region").type(JsonFieldType.STRING).description("지역").optional(),
                                                fieldWithPath("body.difficulty").type(JsonFieldType.STRING).description("러닝 난이도").optional(),
                                                fieldWithPath("body.temperature").type(JsonFieldType.NUMBER).description("사용자 온도"),
                                                fieldWithPath("body.point").type(JsonFieldType.NUMBER).description("포인트"),
                                                fieldWithPath("body.searchBlock").type(JsonFieldType.BOOLEAN).description("검색 차단 여부"),
                                                fieldWithPath("body.profileBlock").type(JsonFieldType.BOOLEAN).description("프로필 차단 여부"),
                                                fieldWithPath("body.notifyBlock").type(JsonFieldType.BOOLEAN).description("알림 차단 여부"),
                                                fieldWithPath("body.createdAt").type(JsonFieldType.STRING).description("생성 일시").optional(),
                                                fieldWithPath("body.updatedAt").type(JsonFieldType.STRING).description("수정 일시").optional()
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void updateProfileTest() throws Exception {
        // given
        Member updatedMember = Member.builder()
                .socialAttr("kakao")
                .socialId("kakao_12345")
                .photo("https://example.com/new-photo.jpg")
                .name("김철수")
                .introduce("업데이트된 자기소개입니다.")
                .age(30)
                .gender(true)
                .region("부산")
                .difficulty("MEDIUM")
                .temperature(36.5)
                .point(100)
                .searchBlock(true)
                .profileBlock(false)
                .notifyBlock(true)
                .build();

        given(memberService.updateProfile(anyLong(), any(MemberUpdateDTO.Request.class))).willReturn(updatedMember);

        // when & then
        this.mockMvc.perform(patch("/api/members/update")
                        .header("Authorization", "Bearer access_token_example")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "photo", "https://example.com/new-photo.jpg",
                                "name", "김철수",
                                "introduce", "업데이트된 자기소개입니다.",
                                "age", 30,
                                "gender", true,
                                "region", "부산",
                                "difficulty", "MEDIUM",
                                "searchBlock", true,
                                "profileBlock", false
                        ))))
                .andExpect(status().isOk())
                .andDo(document("member-update-profile",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .description("로그인한 사용자 프로필 수정")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("photo").type(JsonFieldType.STRING).description("프로필 사진 URL").optional(),
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("닉네임").optional(),
                                                fieldWithPath("introduce").type(JsonFieldType.STRING).description("자기소개").optional(),
                                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이").optional(),
                                                fieldWithPath("gender").type(JsonFieldType.BOOLEAN).description("성별 (true: 남성, false: 여성)").optional(),
                                                fieldWithPath("region").type(JsonFieldType.STRING).description("지역").optional(),
                                                fieldWithPath("difficulty").type(JsonFieldType.STRING).description("러닝 난이도 (EASY, MEDIUM, HARD)").optional(),
                                                fieldWithPath("searchBlock").type(JsonFieldType.BOOLEAN).description("크루 검색 공개 여부").optional(),
                                                fieldWithPath("profileBlock").type(JsonFieldType.BOOLEAN).description("프로필 공개 여부").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional()
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void updateNotificationSettingTest() throws Exception {
        // given
        doNothing().when(memberService).updateNotificationSetting(anyLong(), any(NotificationSettingDTO.Request.class));

        // when & then
        this.mockMvc.perform(patch("/api/members/notification")
                        .header("Authorization", "Bearer access_token_example")
                        .contentType("application/json")
                        .content(toJson(Map.of("notifyBlock", false))))
                .andExpect(status().isOk())
                .andDo(document("member-update-notification",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .summary("알림 설정 수정")
                                        .description("notifyBlock - 알림 차단 여부 (true: 알림 차단, false: 알림 허용")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("notifyBlock").type(JsonFieldType.BOOLEAN).description("알림 차단 여부 (true: 알림 차단/OFF, false: 알림 허용/ON)")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional()
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void withdrawMemberTest() throws Exception {
        // given
        doNothing().when(memberService).withdrawMember(anyLong());

        // when & then
        this.mockMvc.perform(delete("/api/members/withdrawal")
                        .header("Authorization", "Bearer access_token_example"))
                .andExpect(status().isOk())
                .andDo(document("member-withdrawal",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("users")
                                        .summary("회원 탈퇴")
                                        .description("로그인한 사용자의 계정을 탈퇴합니다")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("Bearer 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.NULL).description("응답 본문 (null)").optional()
                                        )
                                        .build()
                        )
                ));
    }
}