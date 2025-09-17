package com.runtracker.domain.record.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.record.dto.RunningRecordDTO;
import com.runtracker.domain.record.service.RecordService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecordControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private RecordService recordService;

    @Test
    void getRunningRecordsSummaryTest() throws Exception {
        // given
        List<RunningRecordDTO> mockRecords = List.of(
                new RunningRecordDTO(
                        1L,
                        1L,
                        1800,
                        LocalDateTime.of(2025, 8, 15, 6, 0, 0),
                        LocalDateTime.of(2025, 8, 15, 6, 30, 0),
                        3000.0,
                        5.5,
                        10.9,
                        250,
                        4500,
                        145,
                        170,
                        165,
                        185,
                        null,
                        null,
                        null
                ),
                new RunningRecordDTO(
                        2L,
                        2L,
                        2400,
                        LocalDateTime.of(2025, 8, 16, 7, 30, 0),
                        LocalDateTime.of(2025, 8, 16, 8, 10, 0),
                        5000.0,
                        4.8,
                        12.5,
                        400,
                        7200,
                        155,
                        180,
                        170,
                        190,
                        null,
                        null,
                        null
                )
        );

        given(recordService.getRunningRecordsSummary(anyLong(), anyString(), any(), any())).willReturn(mockRecords);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/summary")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .param("type", "date")
                        .param("date", "2025-08-15")
                        .param("endDate", "2025-08-16"))
                .andExpect(status().isOk())
                .andDo(document("record-get-running-records-summary",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .summary("러닝 기록 날짜 범위 조회")
                                        .description("지정된 조건에 따라 러닝 기록을 조회합니다. type에 따라 날짜 범위(date), 주간(week), 월간(month) 조회가 가능합니다. ")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("type").description("조회 타입 (date: 날짜 범위, week: 주간, month: 월간)"),
                                                parameterWithName("date").description("기준 날짜 (yyyy-MM-dd)"),
                                                parameterWithName("endDate").description("종료 날짜 (yyyy-MM-dd) - type이 date인 경우에만 필수로 사용").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("러닝 기록 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("러닝 기록 ID"),
                                                fieldWithPath("body[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].runningTime").type(JsonFieldType.NUMBER).description("러닝 시간 (초 단위)"),
                                                fieldWithPath("body[].startedAt").type(JsonFieldType.STRING).description("러닝 시작 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].finishedAt").type(JsonFieldType.STRING).description("러닝 종료 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body[].avgPace").type(JsonFieldType.NUMBER).description("평균 페이스").optional(),
                                                fieldWithPath("body[].avgSpeed").type(JsonFieldType.NUMBER).description("평균 속도").optional(),
                                                fieldWithPath("body[].kcal").type(JsonFieldType.NUMBER).description("소모 칼로리"),
                                                fieldWithPath("body[].walkCnt").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body[].avgHeartRate").type(JsonFieldType.NUMBER).description("평균 심박수").optional(),
                                                fieldWithPath("body[].maxHeartRate").type(JsonFieldType.NUMBER).description("최대 심박수").optional(),
                                                fieldWithPath("body[].avgCadence").type(JsonFieldType.NUMBER).description("평균 케이던스").optional(),
                                                fieldWithPath("body[].maxCadence").type(JsonFieldType.NUMBER).description("최대 케이던스").optional()
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
                2400,
                LocalDateTime.of(2025, 8, 16, 7, 30, 0),
                LocalDateTime.of(2025, 8, 16, 8, 10, 0),
                5000.0,
                4.8,
                12.5,
                400,
                7200,
                155,
                180,
                170,
                190,
                List.of(
                        new com.runtracker.global.vo.Coordinate(37.5665, 126.9780),
                        new com.runtracker.global.vo.Coordinate(37.5670, 126.9785),
                        new com.runtracker.global.vo.Coordinate(37.5675, 126.9790)
                ),
                List.of(
                        new com.runtracker.global.vo.SegmentPace(1000.0, 300),
                        new com.runtracker.global.vo.SegmentPace(1500.0, 450)
                ),
                List.of(
                        List.of(
                                new com.runtracker.global.vo.Coordinate(37.5665, 126.9780),
                                new com.runtracker.global.vo.Coordinate(37.5670, 126.9785)
                        ),
                        List.of(
                                new com.runtracker.global.vo.Coordinate(37.5670, 126.9785),
                                new com.runtracker.global.vo.Coordinate(37.5675, 126.9790)
                        )
                )
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
                                        .summary("러닝 기록 상세 조회")
                                        .description("러닝 기록 ID를 사용하여 특정 기록의 상세 정보를 조회합니다.")
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
                                                fieldWithPath("body.runningTime").type(JsonFieldType.NUMBER).description("러닝 시간 (초 단위)"),
                                                fieldWithPath("body.startedAt").type(JsonFieldType.STRING).description("러닝 시작 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body.finishedAt").type(JsonFieldType.STRING).description("러닝 종료 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body.distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body.avgPace").type(JsonFieldType.NUMBER).description("평균 페이스").optional(),
                                                fieldWithPath("body.avgSpeed").type(JsonFieldType.NUMBER).description("평균 속도").optional(),
                                                fieldWithPath("body.kcal").type(JsonFieldType.NUMBER).description("소모 칼로리"),
                                                fieldWithPath("body.walkCnt").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body.avgHeartRate").type(JsonFieldType.NUMBER).description("평균 심박수").optional(),
                                                fieldWithPath("body.maxHeartRate").type(JsonFieldType.NUMBER).description("최대 심박수").optional(),
                                                fieldWithPath("body.avgCadence").type(JsonFieldType.NUMBER).description("평균 케이던스").optional(),
                                                fieldWithPath("body.maxCadence").type(JsonFieldType.NUMBER).description("최대 케이던스").optional(),
                                                fieldWithPath("body.path").type(JsonFieldType.ARRAY).description("완전한 러닝 경로 (코스 경로 + 사용자 종료 지점 통합)").optional(),
                                                fieldWithPath("body.path[].lat").type(JsonFieldType.NUMBER).description("위도").optional(),
                                                fieldWithPath("body.path[].lnt").type(JsonFieldType.NUMBER).description("경도").optional(),
                                                fieldWithPath("body.segmentPaces").type(JsonFieldType.ARRAY).description("구간별 페이스").optional(),
                                                fieldWithPath("body.segmentPaces[].distance").type(JsonFieldType.NUMBER).description("구간 거리").optional(),
                                                fieldWithPath("body.segmentPaces[].time").type(JsonFieldType.NUMBER).description("구간 시간").optional(),
                                                fieldWithPath("body.segmentPaths").type(JsonFieldType.ARRAY).description("구간별 경로").optional(),
                                                fieldWithPath("body.segmentPaths[].[]").type(JsonFieldType.ARRAY).description("구간 내 좌표 배열").optional(),
                                                fieldWithPath("body.segmentPaths[][].lat").type(JsonFieldType.NUMBER).description("위도").optional(),
                                                fieldWithPath("body.segmentPaths[][].lnt").type(JsonFieldType.NUMBER).description("경도").optional()
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
                        2400,
                        LocalDateTime.of(2025, 8, 16, 7, 30, 0),
                        LocalDateTime.of(2025, 8, 16, 8, 10, 0),
                        5000.0,
                        4.8,
                        12.5,
                        400,
                        7200,
                        155,
                        180,
                        170,
                        190,
                        null,
                        null,
                        null
                ),
                new RunningRecordDTO(
                        2L,
                        2L,
                        1800,
                        LocalDateTime.of(2025, 8, 15, 6, 0, 0),
                        LocalDateTime.of(2025, 8, 15, 6, 30, 0),
                        3000.0,
                        5.5,
                        10.9,
                        250,
                        4500,
                        145,
                        170,
                        165,
                        185,
                        null,
                        null,
                        null
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
                                        .summary("전체 러닝 기록 조회")
                                        .description("사용자의 모든 러닝 기록을 조회합니다. 기록은 러닝 시간 기준 내림차순으로 정렬됩니다.")
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
                                                fieldWithPath("body[].runningTime").type(JsonFieldType.NUMBER).description("러닝 시간 (초 단위)"),
                                                fieldWithPath("body[].startedAt").type(JsonFieldType.STRING).description("러닝 시작 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].finishedAt").type(JsonFieldType.STRING).description("러닝 종료 시간 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("러닝 거리 (미터)"),
                                                fieldWithPath("body[].avgPace").type(JsonFieldType.NUMBER).description("평균 페이스").optional(),
                                                fieldWithPath("body[].avgSpeed").type(JsonFieldType.NUMBER).description("평균 속도").optional(),
                                                fieldWithPath("body[].kcal").type(JsonFieldType.NUMBER).description("소모 칼로리"),
                                                fieldWithPath("body[].walkCnt").type(JsonFieldType.NUMBER).description("걸음 수"),
                                                fieldWithPath("body[].avgHeartRate").type(JsonFieldType.NUMBER).description("평균 심박수").optional(),
                                                fieldWithPath("body[].maxHeartRate").type(JsonFieldType.NUMBER).description("최대 심박수").optional(),
                                                fieldWithPath("body[].avgCadence").type(JsonFieldType.NUMBER).description("평균 케이던스").optional(),
                                                fieldWithPath("body[].maxCadence").type(JsonFieldType.NUMBER).description("최대 케이던스").optional()
                                        )
                                        .build()
                        )
                ));
    }
}