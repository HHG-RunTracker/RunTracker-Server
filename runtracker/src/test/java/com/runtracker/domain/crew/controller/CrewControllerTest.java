package com.runtracker.domain.crew.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCourseRecommendationDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.dto.CrewDetailDTO;
import com.runtracker.domain.crew.dto.CrewListDTO;
import com.runtracker.domain.crew.dto.CrewManagementDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.dto.MemberProfileDTO;
import com.runtracker.domain.crew.dto.CrewRunningDTO;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.enums.CrewRunningStatus;
import com.runtracker.domain.crew.enums.ParticipantStatus;
import com.runtracker.domain.crew.service.CrewService;
import com.runtracker.domain.crew.service.CrewRunningService;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private CrewService crewService;
    
    @MockitoBean
    private CrewRunningService crewRunningService;

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
    void getRecommendedCourses() throws Exception {
        // given
        given(jwtUtil.getMemberIdFromToken(any())).willReturn(600L);
        given(jwtUtil.getSocialIdFromToken(any())).willReturn("kakao_600");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(600L)
                .socialId("kakao_600")
                .roles(List.of(MemberRole.USER, MemberRole.CREW_MANAGER))
                .build();
        given(userDetailsService.loadUserByUsername("600")).willReturn(mockUserDetails);

        List<CrewCourseRecommendationDTO.Response> mockRecommendations = List.of(
                CrewCourseRecommendationDTO.Response.builder()
                        .courseId(1L)
                        .name("한강 러닝 코스")
                        .region("서울시 강남구")
                        .distance(5.2)
                        .difficulty(Difficulty.EASY)
                        .startLat(37.5172)
                        .startLng(127.0473)
                        .photo("https://example.com/course1.jpg")
                        .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                        .build(),
                CrewCourseRecommendationDTO.Response.builder()
                        .courseId(2L)
                        .name("올림픽공원 코스")
                        .region("서울시 송파구")
                        .distance(3.8)
                        .difficulty(Difficulty.MEDIUM)
                        .startLat(37.5219)
                        .startLng(127.1277)
                        .photo("https://example.com/course2.jpg")
                        .createdAt(LocalDateTime.of(2024, 1, 5, 14, 30))
                        .build(),
                CrewCourseRecommendationDTO.Response.builder()
                        .courseId(3L)
                        .name("남산 순환 코스")
                        .region("서울시 중구")
                        .distance(7.1)
                        .difficulty(Difficulty.HARD)
                        .startLat(37.5506)
                        .startLng(126.9910)
                        .photo("https://example.com/course3.jpg")
                        .createdAt(LocalDateTime.of(2024, 1, 10, 16, 45))
                        .build()
        );

        given(crewRunningService.getRecommendedCourses(anyLong(), anyString(), any(), any(), any(UserDetailsImpl.class)))
                .willReturn(mockRecommendations);

        // when
        this.mockMvc.perform(get("/api/crew/{crewId}/recommended-courses", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .param("region", "서울시 강남구")
                        .param("minDistance", "3.0")
                        .param("maxDistance", "8.0"))
                .andExpect(status().isOk())
                .andDo(document("crew-recommended-courses",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew")
                                        .description("크루 코스 추천 조회 (크루장/매니저 전용)")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("조회할 크루 ID")
                                        )
                                        .queryParameters(
                                                parameterWithName("region").description("지역 필터 (선택사항)").optional(),
                                                parameterWithName("minDistance").description("최소 거리 필터 (선택사항)").optional(),
                                                parameterWithName("maxDistance").description("최대 거리 필터 (선택사항)").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("추천 코스 목록"),
                                                fieldWithPath("body[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("body[].region").type(JsonFieldType.STRING).description("코스 지역"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("코스 거리 (km)"),
                                                fieldWithPath("body[].difficulty").type(JsonFieldType.STRING).description("코스 난이도 (EASY, MEDIUM, HARD)"),
                                                fieldWithPath("body[].startLat").type(JsonFieldType.NUMBER).description("시작 지점 위도"),
                                                fieldWithPath("body[].startLng").type(JsonFieldType.NUMBER).description("시작 지점 경도"),
                                                fieldWithPath("body[].photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("body[].createdAt").type(JsonFieldType.STRING).description("코스 생성일시")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void createCrewRunning() throws Exception {
        // given
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(600L)
                .socialId("kakao_600")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("600")).willReturn(mockUserDetails);

        CrewRunningDTO.CreateRequest request = CrewRunningDTO.CreateRequest.builder()
                .title("주말 한강 런닝")
                .description("날씨 좋으니 다같이 뛰어요!")
                .build();

        // when
        this.mockMvc.perform(post("/api/crew/{crewId}/running/create", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("crew-running-create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew-running")
                                        .description("크루 런닝 방 생성 (크루장/매니저 전용)")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("크루 런닝 제목"),
                                                fieldWithPath("description").type(JsonFieldType.STRING).description("크루 런닝 설명")
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
    void getCrewRunnings() throws Exception {
        // given
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(600L)
                .socialId("kakao_600")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("600")).willReturn(mockUserDetails);

        List<CrewRunningDTO.Response> mockResponse = List.of(
                CrewRunningDTO.Response.builder()
                        .id(1L)
                        .crewId(1L)
                        .creatorId(600L)
                        .creatorName("테스트 사용자")
                        .status(CrewRunningStatus.WAITING)
                        .title("주말 한강 런닝")
                        .description("날씨 좋으니 다같이 뛰어요!")
                        .participants(List.of(
                                CrewRunningDTO.ParticipantInfo.builder()
                                        .memberId(600L)
                                        .memberName("테스트 사용자")
                                        .status(ParticipantStatus.JOINED)
                                        .joinedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                                        .build()
                        ))
                        .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                        .build(),
                CrewRunningDTO.Response.builder()
                        .id(2L)
                        .crewId(1L)
                        .creatorId(601L)
                        .creatorName("매니저")
                        .status(CrewRunningStatus.COMPLETED)
                        .startTime(LocalDateTime.of(2024, 1, 14, 8, 0))
                        .endTime(LocalDateTime.of(2024, 1, 14, 9, 30))
                        .title("아침 러닝")
                        .description("상쾌한 아침 런닝")
                        .participants(List.of(
                                CrewRunningDTO.ParticipantInfo.builder()
                                        .memberId(601L)
                                        .memberName("매니저")
                                        .status(ParticipantStatus.FINISHED)
                                        .joinedAt(LocalDateTime.of(2024, 1, 14, 7, 50))
                                        .startedAt(LocalDateTime.of(2024, 1, 14, 8, 0))
                                        .finishedAt(LocalDateTime.of(2024, 1, 14, 9, 30))
                                        .build(),
                                CrewRunningDTO.ParticipantInfo.builder()
                                        .memberId(602L)
                                        .memberName("크루원")
                                        .status(ParticipantStatus.FINISHED)
                                        .joinedAt(LocalDateTime.of(2024, 1, 14, 7, 55))
                                        .startedAt(LocalDateTime.of(2024, 1, 14, 8, 0))
                                        .finishedAt(LocalDateTime.of(2024, 1, 14, 9, 25))
                                        .build()
                        ))
                        .createdAt(LocalDateTime.of(2024, 1, 14, 7, 45))
                        .build()
        );

        given(crewRunningService.getCrewRunnings(anyLong(), any(UserDetailsImpl.class)))
                .willReturn(mockResponse);

        // when
        this.mockMvc.perform(get("/api/crew/{crewId}/running/list", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-running-list",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew-running")
                                        .description("크루 런닝 방 목록 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("크루 런닝 방 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("크루 런닝 방 ID"),
                                                fieldWithPath("body[].crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("body[].creatorId").type(JsonFieldType.NUMBER).description("생성자 ID"),
                                                fieldWithPath("body[].creatorName").type(JsonFieldType.STRING).description("생성자 이름"),
                                                fieldWithPath("body[].status").type(JsonFieldType.STRING).description("크루 런닝 상태"),
                                                fieldWithPath("body[].startTime").type(JsonFieldType.STRING).description("시작 시간").optional(),
                                                fieldWithPath("body[].endTime").type(JsonFieldType.STRING).description("종료 시간").optional(),
                                                fieldWithPath("body[].title").type(JsonFieldType.STRING).description("크루 런닝 제목"),
                                                fieldWithPath("body[].description").type(JsonFieldType.STRING).description("크루 런닝 설명"),
                                                fieldWithPath("body[].participants").type(JsonFieldType.ARRAY).description("참여자 목록"),
                                                fieldWithPath("body[].participants[].memberId").type(JsonFieldType.NUMBER).description("참여자 ID"),
                                                fieldWithPath("body[].participants[].memberName").type(JsonFieldType.STRING).description("참여자 이름").optional(),
                                                fieldWithPath("body[].participants[].status").type(JsonFieldType.STRING).description("참여자 상태"),
                                                fieldWithPath("body[].participants[].joinedAt").type(JsonFieldType.STRING).description("참여 시간"),
                                                fieldWithPath("body[].participants[].startedAt").type(JsonFieldType.STRING).description("시작 시간").optional(),
                                                fieldWithPath("body[].participants[].finishedAt").type(JsonFieldType.STRING).description("완료 시간").optional(),
                                                fieldWithPath("body[].createdAt").type(JsonFieldType.STRING).description("생성일시")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void joinCrewRunning() throws Exception {
        // given
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(600L)
                .socialId("kakao_600")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("600")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/{crewId}/running/{crewRunningId}/join", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-running-join",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew-running")
                                        .description("크루 런닝 방 참여")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("crewRunningId").description("크루 런닝 방 ID")
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
    void leaveCrewRunning() throws Exception {
        // given
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(600L)
                .socialId("kakao_600")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("600")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/crew/{crewId}/running/{crewRunningId}/leave", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-running-leave",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew-running")
                                        .description("크루 런닝 방 나가기")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("crewRunningId").description("크루 런닝 방 ID")
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
    void getCrewRunningDetail() throws Exception {
        // given
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(600L)
                .socialId("kakao_600")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("600")).willReturn(mockUserDetails);

        CrewRunningDTO.Response mockResponse = CrewRunningDTO.Response.builder()
                .id(1L)
                .crewId(1L)
                .creatorId(600L)
                .creatorName("테스트 사용자")
                .status(CrewRunningStatus.WAITING)
                .title("주말 한강 런닝")
                .description("날씨 좋으니 다같이 뛰어요!")
                .participants(List.of(
                        CrewRunningDTO.ParticipantInfo.builder()
                                .memberId(600L)
                                .memberName("테스트 사용자")
                                .status(ParticipantStatus.JOINED)
                                .joinedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                                .build(),
                        CrewRunningDTO.ParticipantInfo.builder()
                                .memberId(601L)
                                .memberName("참여자1")
                                .status(ParticipantStatus.JOINED)
                                .joinedAt(LocalDateTime.of(2024, 1, 15, 10, 5))
                                .build()
                ))
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        given(crewRunningService.getCrewRunningDetail(anyLong(), anyLong(), any(UserDetailsImpl.class)))
                .willReturn(mockResponse);

        // when
        this.mockMvc.perform(get("/api/crew/{crewId}/running/list/{crewRunningId}", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-running-detail",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew-running")
                                        .description("크루 런닝 방 상세 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("crewRunningId").description("크루 런닝 방 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.OBJECT).description("크루 런닝 상세 정보"),
                                                fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("크루 런닝 방 ID"),
                                                fieldWithPath("body.crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("body.creatorId").type(JsonFieldType.NUMBER).description("생성자 ID"),
                                                fieldWithPath("body.creatorName").type(JsonFieldType.STRING).description("생성자 이름"),
                                                fieldWithPath("body.status").type(JsonFieldType.STRING).description("크루 런닝 상태"),
                                                fieldWithPath("body.startTime").type(JsonFieldType.STRING).description("시작 시간").optional(),
                                                fieldWithPath("body.endTime").type(JsonFieldType.STRING).description("종료 시간").optional(),
                                                fieldWithPath("body.title").type(JsonFieldType.STRING).description("크루 런닝 제목"),
                                                fieldWithPath("body.description").type(JsonFieldType.STRING).description("크루 런닝 설명"),
                                                fieldWithPath("body.participants").type(JsonFieldType.ARRAY).description("참여자 목록"),
                                                fieldWithPath("body.participants[].memberId").type(JsonFieldType.NUMBER).description("참여자 ID"),
                                                fieldWithPath("body.participants[].memberName").type(JsonFieldType.STRING).description("참여자 이름"),
                                                fieldWithPath("body.participants[].status").type(JsonFieldType.STRING).description("참여자 상태"),
                                                fieldWithPath("body.participants[].joinedAt").type(JsonFieldType.STRING).description("참여 시간"),
                                                fieldWithPath("body.participants[].startedAt").type(JsonFieldType.STRING).description("시작 시간").optional(),
                                                fieldWithPath("body.participants[].finishedAt").type(JsonFieldType.STRING).description("완료 시간").optional(),
                                                fieldWithPath("body.createdAt").type(JsonFieldType.STRING).description("생성일시")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void deleteCrewRunning() throws Exception {
        // given
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(500L)
                .socialId("kakao_500")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("500")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(delete("/api/crew/{crewId}/running/{crewRunningId}", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("crew-running-delete",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew-running")
                                        .description("크루 런닝 방 삭제 (크루장/매니저만)")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("crewRunningId").description("크루 런닝 방 ID")
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
}