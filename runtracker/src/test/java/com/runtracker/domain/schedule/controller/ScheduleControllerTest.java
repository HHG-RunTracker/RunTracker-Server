package com.runtracker.domain.schedule.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.schedule.service.ScheduleService;
import com.runtracker.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScheduleControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private ScheduleService scheduleService;

    @Test
    void createScheduleTest() throws Exception {
        // given
        given(scheduleService.createSchedule(any(), any())).willReturn(1L);
        
        // JWT 토큰 Mock
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");
        
        // UserDetails Mock
        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);
        
        // when
        Map<String, Object> request = createScheduleRequest();

        this.mockMvc.perform(post("/api/v1/schedules/create")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("schedule-create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("schedules")
                                        .description("크루 일정 등록")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("date").type(JsonFieldType.STRING).description("일정 날짜 (yyyy-MM-dd HH:mm 형식)"),
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("일정 제목"),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("일정 내용").optional()
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

    private Map<String, Object> createScheduleRequest() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("crewId", 1L);
        request.put("date", "2025-08-20 10:00");
        request.put("title", "한강 러닝 모임");
        request.put("content", "한강공원에서 5km 러닝 후 치킨 먹기");
        
        return request;
    }
}