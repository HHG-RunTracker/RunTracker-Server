package com.runtracker.domain.member.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.global.jwt.dto.TokenDataDto;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
}