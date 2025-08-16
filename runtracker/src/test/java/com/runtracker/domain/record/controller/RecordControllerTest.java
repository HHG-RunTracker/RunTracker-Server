package com.runtracker.domain.record.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.record.dto.RunningRecordDTO;
import com.runtracker.domain.record.service.RecordService;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class RecordControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private RecordService recordService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void saveRunningRecordTest() throws Exception {
        // given
        Map<String, Object> createRequest = new LinkedHashMap<>();
        createRequest.put("courseId", 1L);
        createRequest.put("runningTime", "2025-08-16 07:30:00");
        createRequest.put("distance", 5000.0);
        createRequest.put("walk", 100);
        createRequest.put("calorie", 350);

        doNothing().when(recordService).saveRunningRecord(anyLong(), any(RunningRecordDTO.class));
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/records/save")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andDo(document("record-save-running-record",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("러닝 기록 저장")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("runningTime").type(JsonFieldType.STRING).description("러닝 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("walk").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("calorie").type(JsonFieldType.NUMBER).description("소모 칼로리")
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
    void getRunningRecordsByDateRangeTest() throws Exception {
        // given
        List<RunningRecordDTO> mockRecords = List.of(
                new RunningRecordDTO(
                        1L,
                        1L,
                        LocalDateTime.of(2025, 8, 15, 6, 0, 0),
                        3000.0,
                        4500,
                        250
                ),
                new RunningRecordDTO(
                        2L,
                        2L,
                        LocalDateTime.of(2025, 8, 16, 7, 30, 0),
                        5000.0,
                        7200,
                        400
                )
        );

        given(recordService.getRunningRecordsByDate(anyLong(), any(), any())).willReturn(mockRecords);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/date")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .param("startDate", "2025-08-15")
                        .param("endDate", "2025-08-16"))
                .andExpect(status().isOk())
                .andDo(document("record-get-running-records-by-date-range",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("날짜 범위로 러닝 기록 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("startDate").description("시작 날짜 (yyyy-MM-dd)"),
                                                parameterWithName("endDate").description("종료 날짜 (yyyy-MM-dd)")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("러닝 기록 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("러닝 기록 ID"),
                                                fieldWithPath("body[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].runningTime").type(JsonFieldType.STRING).description("러닝 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body[].walk").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body[].calorie").type(JsonFieldType.NUMBER).description("소모 칼로리")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getRunningRecordsByWeekTest() throws Exception {
        // given
        List<RunningRecordDTO> mockRecords = List.of(
                new RunningRecordDTO(
                        1L,
                        1L,
                        LocalDateTime.of(2025, 8, 11, 8, 0, 0),
                        4000.0,
                        6000,
                        320
                ),
                new RunningRecordDTO(
                        2L,
                        2L,
                        LocalDateTime.of(2025, 8, 13, 18, 30, 0),
                        6000.0,
                        8500,
                        480
                )
        );

        given(recordService.getRunningRecordsByWeek(anyLong(), any())).willReturn(mockRecords);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/week")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .param("weekDate", "2025-08-11"))
                .andExpect(status().isOk())
                .andDo(document("record-get-running-records-by-week",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("주 범위로 러닝 기록 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("weekDate").description("주간 기준 날짜 (yyyy-MM-dd) - 해당 날짜가 포함된 주(월~일)의 기록을 조회")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("러닝 기록 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("러닝 기록 ID"),
                                                fieldWithPath("body[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].runningTime").type(JsonFieldType.STRING).description("러닝 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body[].walk").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body[].calorie").type(JsonFieldType.NUMBER).description("소모 칼로리")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getRunningRecordsByMonthTest() throws Exception {
        // given
        List<RunningRecordDTO> mockRecords = List.of(
                new RunningRecordDTO(
                        1L,
                        1L,
                        LocalDateTime.of(2025, 8, 5, 7, 0, 0),
                        8000.0,
                        12000,
                        650
                ),
                new RunningRecordDTO(
                        2L,
                        2L,
                        LocalDateTime.of(2025, 8, 25, 19, 0, 0),
                        5500.0,
                        8200,
                        420
                )
        );

        given(recordService.getRunningRecordsByMonth(anyLong(), any())).willReturn(mockRecords);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/month")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .param("monthDate", "2025-08-15"))
                .andExpect(status().isOk())
                .andDo(document("record-get-running-records-by-month",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("월별 러닝 기록 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("monthDate").description("월간 기준 날짜 (yyyy-MM-dd) - 해당 날짜가 포함된 월의 기록을 조회")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("러닝 기록 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("러닝 기록 ID"),
                                                fieldWithPath("body[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].runningTime").type(JsonFieldType.STRING).description("러닝 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body[].walk").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body[].calorie").type(JsonFieldType.NUMBER).description("소모 칼로리")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getRunningRecordByIdTest() throws Exception {
        // given
        RunningRecordDTO mockRecord = new RunningRecordDTO(
                1L,
                1L,
                LocalDateTime.of(2025, 8, 16, 7, 30, 0),
                5000.0,
                7200,
                400
        );

        given(recordService.getRunningRecordById(anyLong(), anyLong())).willReturn(mockRecord);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/{recordId}", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("record-get-running-record-by-id",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("러닝 기록 ID로 상세 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("recordId").description("러닝 기록 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.OBJECT).description("러닝 기록 상세"),
                                                fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("러닝 기록 ID"),
                                                fieldWithPath("body.courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body.runningTime").type(JsonFieldType.STRING).description("러닝 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body.distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body.walk").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body.calorie").type(JsonFieldType.NUMBER).description("소모 칼로리")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getAllRunningRecordsTest() throws Exception {
        // given
        List<RunningRecordDTO> mockRecords = List.of(
                new RunningRecordDTO(
                        1L,
                        1L,
                        LocalDateTime.of(2025, 8, 16, 7, 30, 0),
                        5000.0,
                        7200,
                        400
                ),
                new RunningRecordDTO(
                        2L,
                        2L,
                        LocalDateTime.of(2025, 8, 15, 6, 0, 0),
                        3000.0,
                        4500,
                        250
                )
        );

        given(recordService.getAllRunningRecords(anyLong())).willReturn(mockRecords);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/user")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("record-get-all-running-records",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("사용자의 전체 러닝 기록 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("러닝 기록 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("러닝 기록 ID"),
                                                fieldWithPath("body[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].runningTime").type(JsonFieldType.STRING).description("러닝 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body[].walk").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body[].calorie").type(JsonFieldType.NUMBER).description("소모 칼로리")
                                        )
                                        .build()
                        )
                ));
    }
}