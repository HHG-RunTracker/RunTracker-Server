package com.runtracker.domain.record.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.record.dto.RecordDetailDTO;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
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
    void getCourseDetailTest() throws Exception {
        // given
        Long courseId = 1L;
        RecordDetailDTO mockCourseDetail = RecordDetailDTO.builder()
                .id(courseId)
                .memberId(2L)
                .name("뚝섬 러닝 코스")
                .difficulty(Difficulty.MEDIUM)
                .points(List.of(
                        new Coordinate(37.53, 127.066),
                        new Coordinate(37.531, 127.067)
                ))
                .startLat(37.5305)
                .startLng(127.0665)
                .distance(7000.0)
                .round(false)
                .region("서울특별시 성동구")
                .photo("https://example.com/photo2.jpg")
                .photoLat(37.5315)
                .photoLng(127.0675)
                .createdAt(LocalDateTime.of(2025, 8, 9, 3, 45, 54))
                .updatedAt(LocalDateTime.of(2025, 8, 9, 3, 45, 54))
                .build();

        given(recordService.getCourseDetail(anyLong())).willReturn(mockCourseDetail);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/records/{courseId}", courseId)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("record-get-course-detail",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("코스별 러닝 기록 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("courseId").description("코스 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.id").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body.memberId").type(JsonFieldType.NUMBER).description("생성자 회원 ID"),
                                                fieldWithPath("body.name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("body.difficulty").type(JsonFieldType.STRING).description("난이도 (EASY, MEDIUM, HARD)"),
                                                fieldWithPath("body.points").type(JsonFieldType.ARRAY).description("코스 경로 좌표 리스트"),
                                                fieldWithPath("body.points[].lat").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                                fieldWithPath("body.points[].lnt").type(JsonFieldType.NUMBER).description("좌표 경도"),
                                                fieldWithPath("body.startLat").type(JsonFieldType.NUMBER).description("시작 지점 위도"),
                                                fieldWithPath("body.startLng").type(JsonFieldType.NUMBER).description("시작 지점 경도"),
                                                fieldWithPath("body.distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)"),
                                                fieldWithPath("body.round").type(JsonFieldType.BOOLEAN).description("왕복 여부"),
                                                fieldWithPath("body.region").type(JsonFieldType.STRING).description("코스 지역"),
                                                fieldWithPath("body.photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("body.photoLat").type(JsonFieldType.NUMBER).description("사진 촬영 위치 위도").optional(),
                                                fieldWithPath("body.photoLng").type(JsonFieldType.NUMBER).description("사진 촬영 위치 경도").optional(),
                                                fieldWithPath("body.createdAt").type(JsonFieldType.STRING).description("코스 생성 시간"),
                                                fieldWithPath("body.updatedAt").type(JsonFieldType.STRING).description("코스 수정 시간")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getCoursesByDateRangeTest() throws Exception {
        // given
        List<RecordDetailDTO> mockCourses = List.of(
                RecordDetailDTO.builder()
                        .id(1L)
                        .memberId(1L)
                        .name("한강 러닝 코스")
                        .difficulty(Difficulty.EASY)
                        .points(List.of(
                                new Coordinate(37.5512, 126.9882),
                                new Coordinate(37.5523, 126.9891)
                        ))
                        .startLat(37.5512)
                        .startLng(126.9882)
                        .distance(3000.0)
                        .round(false)
                        .region("서울특별시 중구")
                        .photo("https://example.com/photo1.jpg")
                        .photoLat(37.5515)
                        .photoLng(126.9885)
                        .createdAt(LocalDateTime.of(2025, 8, 9, 10, 0, 0))
                        .updatedAt(LocalDateTime.of(2025, 8, 9, 10, 0, 0))
                        .build(),
                RecordDetailDTO.builder()
                        .id(2L)
                        .memberId(1L)
                        .name("뚝섬 러닝 코스")
                        .difficulty(Difficulty.MEDIUM)
                        .points(List.of(
                                new Coordinate(37.53, 127.066),
                                new Coordinate(37.531, 127.067)
                        ))
                        .startLat(37.5305)
                        .startLng(127.0665)
                        .distance(5000.0)
                        .round(true)
                        .region("서울특별시 성동구")
                        .photo("https://example.com/photo2.jpg")
                        .photoLat(37.5315)
                        .photoLng(127.0675)
                        .createdAt(LocalDateTime.of(2025, 8, 10, 14, 30, 0))
                        .updatedAt(LocalDateTime.of(2025, 8, 10, 14, 30, 0))
                        .build()
        );

        given(recordService.getCoursesByDate(anyLong(), any(), any())).willReturn(mockCourses);
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
                        .param("startDate", "2025-08-09")
                        .param("endDate", "2025-08-10"))
                .andExpect(status().isOk())
                .andDo(document("record-get-courses-by-date-range",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("날짜 범위로 러닝 기록(코스) 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("startDate").description("시작 날짜 (YYYY-MM-DD)"),
                                                parameterWithName("endDate").description("종료 날짜 (YYYY-MM-DD)")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("코스 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].memberId").type(JsonFieldType.NUMBER).description("생성자 회원 ID"),
                                                fieldWithPath("body[].name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("body[].difficulty").type(JsonFieldType.STRING).description("난이도 (EASY, MEDIUM, HARD)"),
                                                fieldWithPath("body[].points").type(JsonFieldType.ARRAY).description("코스 경로 좌표 리스트"),
                                                fieldWithPath("body[].points[].lat").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                                fieldWithPath("body[].points[].lnt").type(JsonFieldType.NUMBER).description("좌표 경도"),
                                                fieldWithPath("body[].startLat").type(JsonFieldType.NUMBER).description("시작 지점 위도"),
                                                fieldWithPath("body[].startLng").type(JsonFieldType.NUMBER).description("시작 지점 경도"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)"),
                                                fieldWithPath("body[].round").type(JsonFieldType.BOOLEAN).description("왕복 여부"),
                                                fieldWithPath("body[].region").type(JsonFieldType.STRING).description("코스 지역"),
                                                fieldWithPath("body[].photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("body[].photoLat").type(JsonFieldType.NUMBER).description("사진 촬영 위치 위도").optional(),
                                                fieldWithPath("body[].photoLng").type(JsonFieldType.NUMBER).description("사진 촬영 위치 경도").optional(),
                                                fieldWithPath("body[].createdAt").type(JsonFieldType.STRING).description("코스 생성 시간"),
                                                fieldWithPath("body[].updatedAt").type(JsonFieldType.STRING).description("코스 수정 시간")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getCoursesByWeekTest() throws Exception {
        // given
        List<RecordDetailDTO> mockCourses = List.of(
                RecordDetailDTO.builder()
                        .id(1L)
                        .memberId(1L)
                        .name("올림픽공원 러닝 코스")
                        .difficulty(Difficulty.EASY)
                        .points(List.of(
                                new Coordinate(37.5197, 127.1154),
                                new Coordinate(37.5203, 127.1165)
                        ))
                        .startLat(37.5197)
                        .startLng(127.1154)
                        .distance(4000.0)
                        .round(true)
                        .region("서울특별시 송파구")
                        .photo("https://example.com/photo3.jpg")
                        .photoLat(37.5200)
                        .photoLng(127.1160)
                        .createdAt(LocalDateTime.of(2025, 8, 11, 8, 0, 0))
                        .updatedAt(LocalDateTime.of(2025, 8, 11, 8, 0, 0))
                        .build(),
                RecordDetailDTO.builder()
                        .id(2L)
                        .memberId(1L)
                        .name("청계천 러닝 코스")
                        .difficulty(Difficulty.MEDIUM)
                        .points(List.of(
                                new Coordinate(37.5658, 126.9784),
                                new Coordinate(37.5665, 126.9795)
                        ))
                        .startLat(37.5658)
                        .startLng(126.9784)
                        .distance(6000.0)
                        .round(false)
                        .region("서울특별시 중구")
                        .photo("https://example.com/photo4.jpg")
                        .photoLat(37.5662)
                        .photoLng(126.9790)
                        .createdAt(LocalDateTime.of(2025, 8, 12, 18, 30, 0))
                        .updatedAt(LocalDateTime.of(2025, 8, 12, 18, 30, 0))
                        .build()
        );

        given(recordService.getCoursesByWeek(anyLong(), any())).willReturn(mockCourses);
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
                .andDo(document("record-get-courses-by-week",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("주 범위로 러닝 기록(코스) 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("weekDate").description("주간 기준 날짜 (YYYY-MM-DD) - 해당 날짜가 포함된 주(월~일)의 기록을 조회")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("코스 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].memberId").type(JsonFieldType.NUMBER).description("생성자 회원 ID"),
                                                fieldWithPath("body[].name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("body[].difficulty").type(JsonFieldType.STRING).description("난이도 (EASY, MEDIUM, HARD)"),
                                                fieldWithPath("body[].points").type(JsonFieldType.ARRAY).description("코스 경로 좌표 리스트"),
                                                fieldWithPath("body[].points[].lat").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                                fieldWithPath("body[].points[].lnt").type(JsonFieldType.NUMBER).description("좌표 경도"),
                                                fieldWithPath("body[].startLat").type(JsonFieldType.NUMBER).description("시작 지점 위도"),
                                                fieldWithPath("body[].startLng").type(JsonFieldType.NUMBER).description("시작 지점 경도"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)"),
                                                fieldWithPath("body[].round").type(JsonFieldType.BOOLEAN).description("왕복 여부"),
                                                fieldWithPath("body[].region").type(JsonFieldType.STRING).description("코스 지역"),
                                                fieldWithPath("body[].photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("body[].photoLat").type(JsonFieldType.NUMBER).description("사진 촬영 위치 위도").optional(),
                                                fieldWithPath("body[].photoLng").type(JsonFieldType.NUMBER).description("사진 촬영 위치 경도").optional(),
                                                fieldWithPath("body[].createdAt").type(JsonFieldType.STRING).description("코스 생성 시간"),
                                                fieldWithPath("body[].updatedAt").type(JsonFieldType.STRING).description("코스 수정 시간")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getCoursesByMonthTest() throws Exception {
        // given
        List<RecordDetailDTO> mockCourses = List.of(
                RecordDetailDTO.builder()
                        .id(1L)
                        .memberId(1L)
                        .name("남산 러닝 코스")
                        .difficulty(Difficulty.HARD)
                        .points(List.of(
                                new Coordinate(37.5512, 126.9910),
                                new Coordinate(37.5520, 126.9920)
                        ))
                        .startLat(37.5512)
                        .startLng(126.9910)
                        .distance(8000.0)
                        .round(true)
                        .region("서울특별시 중구")
                        .photo("https://example.com/photo5.jpg")
                        .photoLat(37.5516)
                        .photoLng(126.9915)
                        .createdAt(LocalDateTime.of(2025, 8, 5, 7, 0, 0))
                        .updatedAt(LocalDateTime.of(2025, 8, 5, 7, 0, 0))
                        .build(),
                RecordDetailDTO.builder()
                        .id(2L)
                        .memberId(1L)
                        .name("잠실 러닝 코스")
                        .difficulty(Difficulty.MEDIUM)
                        .points(List.of(
                                new Coordinate(37.5145, 127.1025),
                                new Coordinate(37.5155, 127.1035)
                        ))
                        .startLat(37.5145)
                        .startLng(127.1025)
                        .distance(5500.0)
                        .round(false)
                        .region("서울특별시 송파구")
                        .photo("https://example.com/photo6.jpg")
                        .photoLat(37.5150)
                        .photoLng(127.1030)
                        .createdAt(LocalDateTime.of(2025, 8, 25, 19, 0, 0))
                        .updatedAt(LocalDateTime.of(2025, 8, 25, 19, 0, 0))
                        .build()
        );

        given(recordService.getCoursesByMonth(anyLong(), any())).willReturn(mockCourses);
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
                .andDo(document("record-get-courses-by-month",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("records")
                                        .description("월별 러닝 기록(코스) 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("monthDate").description("월간 기준 날짜 (YYYY-MM-DD) - 해당 날짜가 포함된 월의 기록을 조회")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("코스 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].memberId").type(JsonFieldType.NUMBER).description("생성자 회원 ID"),
                                                fieldWithPath("body[].name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("body[].difficulty").type(JsonFieldType.STRING).description("난이도 (EASY, MEDIUM, HARD)"),
                                                fieldWithPath("body[].points").type(JsonFieldType.ARRAY).description("코스 경로 좌표 리스트"),
                                                fieldWithPath("body[].points[].lat").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                                fieldWithPath("body[].points[].lnt").type(JsonFieldType.NUMBER).description("좌표 경도"),
                                                fieldWithPath("body[].startLat").type(JsonFieldType.NUMBER).description("시작 지점 위도"),
                                                fieldWithPath("body[].startLng").type(JsonFieldType.NUMBER).description("시작 지점 경도"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)"),
                                                fieldWithPath("body[].round").type(JsonFieldType.BOOLEAN).description("왕복 여부"),
                                                fieldWithPath("body[].region").type(JsonFieldType.STRING).description("코스 지역"),
                                                fieldWithPath("body[].photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("body[].photoLat").type(JsonFieldType.NUMBER).description("사진 촬영 위치 위도").optional(),
                                                fieldWithPath("body[].photoLng").type(JsonFieldType.NUMBER).description("사진 촬영 위치 경도").optional(),
                                                fieldWithPath("body[].createdAt").type(JsonFieldType.STRING).description("코스 생성 시간"),
                                                fieldWithPath("body[].updatedAt").type(JsonFieldType.STRING).description("코스 수정 시간")
                                        )
                                        .build()
                        )
                ));
    }
}