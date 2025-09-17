package com.runtracker.domain.crew.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.course.enums.Difficulty;
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
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.service.CrewService;
import com.runtracker.domain.crew.service.CrewRankingService;
import com.runtracker.domain.crew.service.CrewMemberRankingService;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.mockito.Mockito.verify;

class CrewControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private CrewService crewService;

    @MockitoBean
    private CrewRankingService crewRankingService;

    @MockitoBean
    private CrewMemberRankingService crewMemberRankingService;

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
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
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
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
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
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
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
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
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
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
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

    @Test
    void leaveCrew() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(444L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_444");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(444L)
                .socialId("kakao_444")
                .roles(List.of(MemberRole.USER, MemberRole.CREW_MEMBER))
                .build();
        given(userDetailsService.loadUserByUsername("444")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/leave/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-leave",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 나가기")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("나갈 크루 ID")
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
    void getCrewList() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(200L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_200");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(200L)
                .socialId("kakao_200")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("200")).willReturn(mockUserDetails);

        List<CrewListDTO.Response> mockCrews = List.of(
                CrewListDTO.Response.builder()
                        .id(1L)
                        .title("러닝크루 초급자")
                        .photo("https://example.com/crew1.jpg")
                        .introduce("초급자를 위한 러닝크루입니다.")
                        .region("서울시 강남구")
                        .difficulty(Difficulty.EASY)
                        .leaderId(123L)
                        .memberCount(5)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .build(),
                CrewListDTO.Response.builder()
                        .id(2L)
                        .title("런닝 매니아")
                        .photo("https://example.com/crew2.jpg")
                        .introduce("고급 러너를 위한 크루입니다.")
                        .region("서울시 서초구")
                        .difficulty(Difficulty.HARD)
                        .leaderId(456L)
                        .memberCount(10)
                        .createdAt(LocalDateTime.of(2024, 1, 15, 14, 30))
                        .build()
        );

        CrewListDTO.ListResponse mockResponse = CrewListDTO.ListResponse.of(mockCrews);
        given(crewService.getAllCrews()).willReturn(mockResponse);

        // when
        this.mockMvc.perform(get("/api/crew/list")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-list",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 리스트 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.crews").type(JsonFieldType.ARRAY).description("크루 목록"),
                                                fieldWithPath("body.crews[].id").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("body.crews[].title").type(JsonFieldType.STRING).description("크루 이름"),
                                                fieldWithPath("body.crews[].photo").type(JsonFieldType.STRING).description("크루 대표 사진 URL").optional(),
                                                fieldWithPath("body.crews[].introduce").type(JsonFieldType.STRING).description("크루 소개").optional(),
                                                fieldWithPath("body.crews[].region").type(JsonFieldType.STRING).description("크루 활동 지역").optional(),
                                                fieldWithPath("body.crews[].difficulty").type(JsonFieldType.STRING).description("크루 난이도 (EASY, MEDIUM, HARD)").optional(),
                                                fieldWithPath("body.crews[].leaderId").type(JsonFieldType.NUMBER).description("크루장 ID"),
                                                fieldWithPath("body.crews[].memberCount").type(JsonFieldType.NUMBER).description("크루 멤버 수"),
                                                fieldWithPath("body.crews[].createdAt").type(JsonFieldType.STRING).description("크루 생성일시"),
                                                fieldWithPath("body.totalCount").type(JsonFieldType.NUMBER).description("전체 크루 개수")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getCrewDetail() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(300L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_300");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(300L)
                .socialId("kakao_300")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("300")).willReturn(mockUserDetails);

        List<CrewDetailDTO.MemberInfo> mockMembers = List.of(
                CrewDetailDTO.MemberInfo.builder()
                        .memberId(123L)
                        .role(MemberRole.CREW_LEADER)
                        .status(CrewMemberStatus.ACTIVE)
                        .joinedAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .build(),
                CrewDetailDTO.MemberInfo.builder()
                        .memberId(456L)
                        .role(MemberRole.CREW_MANAGER)
                        .status(CrewMemberStatus.ACTIVE)
                        .joinedAt(LocalDateTime.of(2024, 1, 5, 14, 30))
                        .build(),
                CrewDetailDTO.MemberInfo.builder()
                        .memberId(789L)
                        .role(MemberRole.CREW_MEMBER)
                        .status(CrewMemberStatus.ACTIVE)
                        .joinedAt(LocalDateTime.of(2024, 1, 10, 16, 45))
                        .build()
        );

        CrewDetailDTO.Response mockResponse = CrewDetailDTO.Response.builder()
                .id(1L)
                .title("러닝크루 초급자")
                .photo("https://example.com/crew-detail.jpg")
                .introduce("초급자를 위한 러닝크루입니다. 함께 건강한 러닝 습관을 만들어 봐요!")
                .region("서울시 강남구")
                .difficulty(Difficulty.EASY)
                .schedules("매주 화, 목 오후 7시")
                .leaderId(123L)
                .totalMemberCount(5)
                .activeMemberCount(3)
                .members(mockMembers)
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        given(crewService.getCrewDetail(1L)).willReturn(mockResponse);

        // when
        this.mockMvc.perform(get("/api/crew/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-detail",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 상세 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("조회할 크루 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("body.title").type(JsonFieldType.STRING).description("크루 이름"),
                                                fieldWithPath("body.photo").type(JsonFieldType.STRING).description("크루 대표 사진 URL").optional(),
                                                fieldWithPath("body.introduce").type(JsonFieldType.STRING).description("크루 소개").optional(),
                                                fieldWithPath("body.region").type(JsonFieldType.STRING).description("크루 활동 지역").optional(),
                                                fieldWithPath("body.difficulty").type(JsonFieldType.STRING).description("크루 난이도 (EASY, MEDIUM, HARD)").optional(),
                                                fieldWithPath("body.schedules").type(JsonFieldType.STRING).description("크루 활동 일정").optional(),
                                                fieldWithPath("body.leaderId").type(JsonFieldType.NUMBER).description("크루장 ID"),
                                                fieldWithPath("body.totalMemberCount").type(JsonFieldType.NUMBER).description("전체 멤버 수"),
                                                fieldWithPath("body.activeMemberCount").type(JsonFieldType.NUMBER).description("활성 멤버 수"),
                                                fieldWithPath("body.members").type(JsonFieldType.ARRAY).description("활성 멤버 목록"),
                                                fieldWithPath("body.members[].memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                                                fieldWithPath("body.members[].role").type(JsonFieldType.STRING).description("멤버 권한 (CREW_LEADER, CREW_MANAGER, CREW_MEMBER)"),
                                                fieldWithPath("body.members[].status").type(JsonFieldType.STRING).description("멤버 상태 (ACTIVE, PENDING, BANNED)"),
                                                fieldWithPath("body.members[].joinedAt").type(JsonFieldType.STRING).description("가입일시"),
                                                fieldWithPath("body.createdAt").type(JsonFieldType.STRING).description("크루 생성일시")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getPendingMembers() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(333L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_333");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(333L)
                .socialId("kakao_333")
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("333")).willReturn(mockUserDetails);

        List<CrewManagementDTO.MemberInfo> mockPendingMembers = List.of(
                CrewManagementDTO.MemberInfo.builder()
                        .memberId(111L)
                        .name("김러너")
                        .age(25)
                        .gender(true)
                        .role(MemberRole.USER)
                        .status(CrewMemberStatus.PENDING)
                        .requestedAt(LocalDateTime.of(2024, 1, 20, 10, 0))
                        .build(),
                CrewManagementDTO.MemberInfo.builder()
                        .memberId(222L)
                        .name("이조거")
                        .age(30)
                        .gender(false)
                        .role(MemberRole.USER)
                        .status(CrewMemberStatus.PENDING)
                        .requestedAt(LocalDateTime.of(2024, 1, 21, 14, 30))
                        .build()
        );

        CrewManagementDTO.PendingMembersResponse mockResponse = CrewManagementDTO.PendingMembersResponse.of(mockPendingMembers);
        given(crewService.getPendingMembers(anyLong(), any(UserDetailsImpl.class))).willReturn(mockResponse);

        // when
        this.mockMvc.perform(get("/api/crew/list/pending/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-pending-members",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 가입 요청자 목록 조회 (크루장/매니저 전용)")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("조회할 크루 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.pendingMembers").type(JsonFieldType.ARRAY).description("가입 요청 대기 멤버 목록"),
                                                fieldWithPath("body.pendingMembers[].memberId").type(JsonFieldType.NUMBER).description("요청자 멤버 ID"),
                                                fieldWithPath("body.pendingMembers[].name").type(JsonFieldType.STRING).description("요청자 이름"),
                                                fieldWithPath("body.pendingMembers[].age").type(JsonFieldType.NUMBER).description("요청자 나이"),
                                                fieldWithPath("body.pendingMembers[].gender").type(JsonFieldType.BOOLEAN).description("요청자 성별 (true: 남성, false: 여성)"),
                                                fieldWithPath("body.pendingMembers[].role").type(JsonFieldType.STRING).description("멤버 권한 (USER)"),
                                                fieldWithPath("body.pendingMembers[].status").type(JsonFieldType.STRING).description("멤버 상태 (PENDING)"),
                                                fieldWithPath("body.pendingMembers[].requestedAt").type(JsonFieldType.STRING).description("가입 신청일시"),
                                                fieldWithPath("body.totalCount").type(JsonFieldType.NUMBER).description("총 가입 요청자 수")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getBannedMembers() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(333L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_333");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(333L)
                .socialId("kakao_333")
                .roles(List.of(MemberRole.USER, MemberRole.CREW_LEADER))
                .build();
        given(userDetailsService.loadUserByUsername("333")).willReturn(mockUserDetails);

        List<CrewManagementDTO.MemberInfo> mockBannedMembers = List.of(
                CrewManagementDTO.MemberInfo.builder()
                        .memberId(999L)
                        .name("박차단")
                        .age(28)
                        .gender(true)
                        .role(MemberRole.CREW_MEMBER)
                        .status(CrewMemberStatus.BANNED)
                        .requestedAt(LocalDateTime.of(2024, 1, 10, 10, 0))
                        .build(),
                CrewManagementDTO.MemberInfo.builder()
                        .memberId(888L)
                        .name("최추방")
                        .age(35)
                        .gender(false)
                        .role(MemberRole.CREW_MEMBER)
                        .status(CrewMemberStatus.BANNED)
                        .requestedAt(LocalDateTime.of(2024, 1, 15, 16, 30))
                        .build()
        );

        CrewManagementDTO.BannedMembersResponse mockResponse = CrewManagementDTO.BannedMembersResponse.of(mockBannedMembers);
        given(crewService.getBannedMembers(anyLong(), any(UserDetailsImpl.class))).willReturn(mockResponse);

        // when
        this.mockMvc.perform(get("/api/crew/list/banned/{crewId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-banned-members",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 차단된 멤버 목록 조회 (크루장/매니저 전용)")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("조회할 크루 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.bannedMembers").type(JsonFieldType.ARRAY).description("차단된 멤버 목록"),
                                                fieldWithPath("body.bannedMembers[].memberId").type(JsonFieldType.NUMBER).description("차단된 멤버 ID"),
                                                fieldWithPath("body.bannedMembers[].name").type(JsonFieldType.STRING).description("차단된 멤버 이름"),
                                                fieldWithPath("body.bannedMembers[].age").type(JsonFieldType.NUMBER).description("차단된 멤버 나이"),
                                                fieldWithPath("body.bannedMembers[].gender").type(JsonFieldType.BOOLEAN).description("차단된 멤버 성별 (true: 남성, false: 여성)"),
                                                fieldWithPath("body.bannedMembers[].role").type(JsonFieldType.STRING).description("멤버 권한 (CREW_MEMBER, CREW_MANAGER)"),
                                                fieldWithPath("body.bannedMembers[].status").type(JsonFieldType.STRING).description("멤버 상태 (BANNED)"),
                                                fieldWithPath("body.bannedMembers[].requestedAt").type(JsonFieldType.STRING).description("최초 가입일시"),
                                                fieldWithPath("body.totalCount").type(JsonFieldType.NUMBER).description("총 차단된 멤버 수")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getMemberProfile() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(500L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_500");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(500L)
                .socialId("kakao_500")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("500")).willReturn(mockUserDetails);

        MemberProfileDTO mockProfile = MemberProfileDTO.builder()
                .memberId(123L)
                .socialId("kakao_123")
                .photo("https://example.com/profile123.jpg")
                .name("김런닝")
                .introduce("건강한 러닝을 좋아하는 김런닝입니다!")
                .age(28)
                .gender(true)
                .region("서울시 강남구")
                .difficulty("MEDIUM")
                .temperature(36.5)
                .build();

        given(crewService.getMemberProfile(anyLong(), any(UserDetailsImpl.class))).willReturn(mockProfile);

        // when
        this.mockMvc.perform(get("/api/crew/member/{memberId}", 123L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("member-profile",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("유저 프로필 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("memberId").description("조회할 멤버 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                                                fieldWithPath("body.socialId").type(JsonFieldType.STRING).description("소셜 ID"),
                                                fieldWithPath("body.photo").type(JsonFieldType.STRING).description("프로필 사진 URL").optional(),
                                                fieldWithPath("body.name").type(JsonFieldType.STRING).description("이름"),
                                                fieldWithPath("body.introduce").type(JsonFieldType.STRING).description("자기소개").optional(),
                                                fieldWithPath("body.age").type(JsonFieldType.NUMBER).description("나이"),
                                                fieldWithPath("body.gender").type(JsonFieldType.BOOLEAN).description("성별 (true: 남성, false: 여성)"),
                                                fieldWithPath("body.region").type(JsonFieldType.STRING).description("지역").optional(),
                                                fieldWithPath("body.difficulty").type(JsonFieldType.STRING).description("선호 난이도").optional(),
                                                fieldWithPath("body.temperature").type(JsonFieldType.NUMBER).description("매너 온도").optional()
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getCrewRankingList() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(123L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(123L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();

        // Mock ranking data
        List<CrewRankingDTO.CrewRankInfo> rankings = List.of(
                CrewRankingDTO.CrewRankInfo.builder()
                        .crewId(1L)
                        .crewName("러닝크루A")
                        .crewPhoto("photo1.jpg")
                        .rank(1)
                        .totalDistance(25000.0)
                        .totalRunningTime(7200)
                        .build(),
                CrewRankingDTO.CrewRankInfo.builder()
                        .crewId(2L)
                        .crewName("러닝크루B")
                        .crewPhoto("photo2.jpg")
                        .rank(2)
                        .totalDistance(18000.0)
                        .totalRunningTime(5400)
                        .build()
        );

        CrewRankingDTO.Response response = CrewRankingDTO.Response.builder()
                .date(LocalDate.now())
                .rankings(rankings)
                .lastUpdated(LocalDateTime.now())
                .build();

        given(crewRankingService.getDailyRanking(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/crew/ranking/list")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andDo(document("crew-ranking-list",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Crew-ranking")
                                .summary("크루 랭킹 조회")
                                .description("오늘 날짜 기준 크루 랭킹을 조회합니다. (일주일 기준)")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰")
                                )
                                .responseFields(
                                        fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                        fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                        fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("랭킹 응답"),
                                        fieldWithPath("body.date").type(JsonFieldType.STRING).description("랭킹 날짜"),
                                        fieldWithPath("body.lastUpdated").type(JsonFieldType.STRING).description("최종 업데이트 시간"),
                                        fieldWithPath("body.rankings").type(JsonFieldType.ARRAY).description("랭킹 리스트"),
                                        fieldWithPath("body.rankings[].crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                                        fieldWithPath("body.rankings[].crewName").type(JsonFieldType.STRING).description("크루 이름"),
                                        fieldWithPath("body.rankings[].crewPhoto").type(JsonFieldType.STRING).description("크루 사진").optional(),
                                        fieldWithPath("body.rankings[].rank").type(JsonFieldType.NUMBER).description("순위"),
                                        fieldWithPath("body.rankings[].totalDistance").type(JsonFieldType.NUMBER).description("총 거리 (미터)"),
                                        fieldWithPath("body.rankings[].totalRunningTime").type(JsonFieldType.NUMBER).description("총 런닝 시간 (초)")
                                )
                                .build()
                        )
                ));
    }

    @Test
    void recalculateCrewRanking() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(123L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(123L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();

        // when & then
        mockMvc.perform(post("/api/crew/ranking/recalculate")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andDo(document("crew-ranking-recalculate",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Crew-ranking")
                                .summary("크루 랭킹 수동 재계산")
                                .description("오늘 날짜 기준 크루 랭킹을 강제로 재계산합니다. (랭킹 조회시 자동으로 랭킹 계산해주지만 필요시 사용)")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰")
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
    void getCrewMemberRanking() throws Exception {
        // given
        Long crewId = 1L;
        LocalDate date = LocalDate.of(2024, 8, 15);

        CrewMemberRankingDTO.Response response = CrewMemberRankingDTO.Response.builder()
                .date(date)
                .crewId(crewId)
                .crewName("테스트 크루")
                .rankings(List.of(
                        CrewMemberRankingDTO.MemberRankInfo.builder()
                                .memberId(1L)
                                .memberName("김철수")
                                .memberPhoto("photo1.jpg")
                                .rank(1)
                                .totalDistance(25.5)
                                .totalRunningTime(7200)
                                .averageDistance(8.5)
                                .averageRunningTime(2400)
                                .build(),
                        CrewMemberRankingDTO.MemberRankInfo.builder()
                                .memberId(2L)
                                .memberName("이영희")
                                .memberPhoto("photo2.jpg")
                                .rank(2)
                                .totalDistance(18.0)
                                .totalRunningTime(5400)
                                .averageDistance(9.0)
                                .averageRunningTime(2700)
                                .build()
                ))
                .lastUpdated(LocalDateTime.of(2024, 8, 15, 12, 30, 0))
                .build();

        given(crewMemberRankingService.getCrewMemberRanking(crewId, LocalDate.now())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/crew/{crewId}/member-ranking", crewId)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andDo(document("crew-member-ranking-get",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Crew-ranking")
                                .summary("크루 멤버 랭킹 조회")
                                .description("특정 크루 내 멤버들의 개별 랭킹을 조회합니다 (일주일 기준)")
                                .pathParameters(
                                        parameterWithName("crewId").description("크루 ID")
                                )
                                .responseFields(
                                        fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                        fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                        fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                        fieldWithPath("body.date").type(JsonFieldType.STRING).description("랭킹 기준 날짜"),
                                        fieldWithPath("body.crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                                        fieldWithPath("body.crewName").type(JsonFieldType.STRING).description("크루 이름"),
                                        fieldWithPath("body.rankings").type(JsonFieldType.ARRAY).description("멤버 랭킹 목록"),
                                        fieldWithPath("body.rankings[].memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                                        fieldWithPath("body.rankings[].memberName").type(JsonFieldType.STRING).description("멤버 이름"),
                                        fieldWithPath("body.rankings[].memberPhoto").type(JsonFieldType.STRING).description("멤버 프로필 사진").optional(),
                                        fieldWithPath("body.rankings[].rank").type(JsonFieldType.NUMBER).description("랭킹 순위"),
                                        fieldWithPath("body.rankings[].totalDistance").type(JsonFieldType.NUMBER).description("총 거리"),
                                        fieldWithPath("body.rankings[].totalRunningTime").type(JsonFieldType.NUMBER).description("총 러닝 시간"),
                                        fieldWithPath("body.rankings[].averageDistance").type(JsonFieldType.NUMBER).description("평균 거리"),
                                        fieldWithPath("body.rankings[].averageRunningTime").type(JsonFieldType.NUMBER).description("평균 러닝 시간"),
                                        fieldWithPath("body.lastUpdated").type(JsonFieldType.STRING).description("마지막 업데이트 시간")
                                )
                                .build()
                        )
                ));

        verify(crewMemberRankingService).getCrewMemberRanking(crewId, LocalDate.now());
    }

    @Test
    void recalculateCrewMemberRanking() throws Exception {
        // given
        Long crewId = 1L;
        LocalDate date = LocalDate.of(2024, 8, 15);

        // when & then
        mockMvc.perform(post("/api/crew/{crewId}/member-ranking/recalculate", crewId)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andDo(document("crew-member-ranking-recalculate",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Crew-ranking")
                                .summary("크루 멤버 랭킹 수동 재계산")
                                .description("특정 크루 내 멤버들의 랭킹을 강제로 재계산합니다. (랭킹 조회시 자동으로 랭킹 계산해주지만 필요시 사용)")
                                .pathParameters(
                                        parameterWithName("crewId").description("크루 ID")
                                )
                                .responseFields(
                                        fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                        fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                        fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional()
                                )
                                .build()
                        )
                ));

        verify(crewMemberRankingService).recalculateCrewMemberRanking(crewId, LocalDate.now());
    }
}