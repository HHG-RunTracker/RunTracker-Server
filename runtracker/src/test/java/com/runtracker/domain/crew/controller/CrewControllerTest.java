package com.runtracker.domain.crew.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.service.CrewService;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private CrewService crewService;

    @Test
    void createCrew() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(123L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(123L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("123")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/create")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(CrewCreateDTO.Request.builder()
                                .title("러닝크루 초급자")
                                .photo("https://example.com/crew-photo.jpg")
                                .introduce("초급자를 위한 러닝크루입니다. 함께 건강한 러닝 습관을 만들어 봐요!")
                                .region("서울시 강남구")
                                .difficulty(Difficulty.EASY)
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("crew-create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 생성")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("크루 이름"),
                                                fieldWithPath("photo").type(JsonFieldType.STRING).description("크루 대표 사진 URL").optional(),
                                                fieldWithPath("introduce").type(JsonFieldType.STRING).description("크루 소개").optional(),
                                                fieldWithPath("region").type(JsonFieldType.STRING).description("크루 활동 지역").optional(),
                                                fieldWithPath("difficulty").type(JsonFieldType.STRING).description("크루 난이도 (EASY, MEDIUM, HARD)")
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
    void applyToJoinCrew() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(456L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_456");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(456L)
                .socialId("kakao_456")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("456")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/join/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-join",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 가입 신청")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
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
    void cancelCrewApplication() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(789L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_789");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(789L)
                .socialId("kakao_789")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("789")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/join/cancel/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-cancel-application",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 가입 신청 취소")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
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
    void processJoinRequest() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(999L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_999");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(999L)
                .socialId("kakao_999")
                .roles(List.of(MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("999")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/approval/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(CrewApprovalDTO.Request.builder()
                                .memberId(456L)
                                .approved(true)
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("crew-process-join-request",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 가입 승인/거절")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("신청자 회원 ID"),
                                                fieldWithPath("approved").type(JsonFieldType.BOOLEAN).description("승인 유무 (true: 승인, false: 거절)")
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
    void updateCrewMemberRole() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(888L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_888");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(888L)
                .socialId("kakao_888")
                .roles(List.of(MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("888")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/member/role/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(CrewMemberUpdateDTO.Request.builder()
                                .memberId(456L)
                                .role(MemberRole.CREW_MANAGER)
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("crew-update-member-role",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루원 권한 수정")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("대상 회원 ID"),
                                                fieldWithPath("role").type(JsonFieldType.STRING).description("변경할 권한 (CREW_MEMBER, CREW_MANAGER)")
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
    void updateCrew() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(777L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_777");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(777L)
                .socialId("kakao_777")
                .roles(List.of(MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("777")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(patch("/api/crew/update/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(CrewUpdateDTO.Request.builder()
                                .title("업데이트된 크루명")
                                .photo("https://example.com/updated-photo.jpg")
                                .introduce("업데이트된 크루 소개입니다.")
                                .region("서울시 서초구")
                                .difficulty(Difficulty.MEDIUM)
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("crew-update",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 정보 수정")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("크루 이름").optional(),
                                                fieldWithPath("photo").type(JsonFieldType.STRING).description("크루 대표 사진 URL").optional(),
                                                fieldWithPath("introduce").type(JsonFieldType.STRING).description("크루 소개").optional(),
                                                fieldWithPath("region").type(JsonFieldType.STRING).description("크루 활동 지역").optional(),
                                                fieldWithPath("difficulty").type(JsonFieldType.STRING).description("크루 난이도 (EASY, MEDIUM, HARD)").optional()
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
    void deleteCrew() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(666L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_666");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(666L)
                .socialId("kakao_666")
                .roles(List.of(MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("666")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(delete("/api/crew/delete/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-delete",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 삭제")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
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
    void banCrewMember() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(555L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_555");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(555L)
                .socialId("kakao_555")
                .roles(List.of(MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("555")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/ban/{crewId}?memberId=123", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-ban-member",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루원 차단")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("memberId").description("차단할 회원 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional()
                                        )
                                        .build()
                        )));
    }
}