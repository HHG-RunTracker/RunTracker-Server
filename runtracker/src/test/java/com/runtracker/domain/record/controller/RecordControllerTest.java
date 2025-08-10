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
}