package com.runtracker.domain.course.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.course.dto.CourseCreateDTO;
import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.dto.NearbyCoursesDTO;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.course.entity.vo.Coordinate;
import com.runtracker.domain.course.service.CourseService;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.course.dto.FinishRunning;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private CourseService courseService;

    @Test
    void saveCourseTest() throws Exception {
        given(courseService.saveCourse(anyLong(), any(CourseCreateDTO.class))).willReturn(null);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);
        
        // when
        Map<String, Object> request = createCourseRequest();

        this.mockMvc.perform(post("/api/courses/save")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("course-save",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("courses")
                                        .description("코스 저장")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("path").type(JsonFieldType.ARRAY).description("코스 경로 좌표 리스트"),
                                                fieldWithPath("path[].lat").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                                fieldWithPath("path[].lnt").type(JsonFieldType.NUMBER).description("좌표 경도"),
                                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)")
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

    private Map<String, Object> createCourseRequest() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("name", "한강 러닝 코스");

        Map<String, Object> point1 = new LinkedHashMap<>();
        point1.put("lat", 37.5512);
        point1.put("lnt", 126.9882);
        
        Map<String, Object> point2 = new LinkedHashMap<>();
        point2.put("lat", 37.5523);
        point2.put("lnt", 126.9891);
        
        request.put("path", List.of(point1, point2));
        request.put("distance", 5000.0);
        
        return request;
    }

    @Test
    void getNearbyCoursesTest() throws Exception {
        // given
        List<NearbyCoursesDTO.Response> mockCourses = List.of(
                NearbyCoursesDTO.Response.builder()
                        .id(1L)
                        .memberId(2L)
                        .name("한강공원 러닝 코스")
                        .difficulty(Difficulty.EASY)
                        .points(List.of(
                                new Coordinate(37.5512, 126.9882),
                                new Coordinate(37.5523, 126.9891)
                        ))
                        .startLat(37.5512)
                        .startLng(126.9882)
                        .distance(3000.0)
                        .round(false)
                        .region("서울특별시 영등포구")
                        .photo("https://example.com/photo1.jpg")
                        .photoLat(37.5515)
                        .photoLng(126.9885)
                        .distanceFromUser(1200.5)
                        .build(),
                NearbyCoursesDTO.Response.builder()
                        .id(2L)
                        .memberId(3L)
                        .name("여의도 산책 코스")
                        .difficulty(Difficulty.MEDIUM)
                        .points(List.of(
                                new Coordinate(37.5665, 126.9780),
                                new Coordinate(37.5670, 126.9785)
                        ))
                        .startLat(37.5665)
                        .startLng(126.9780)
                        .distance(4500.0)
                        .round(true)
                        .region("서울특별시 영등포구")
                        .photo("https://example.com/photo2.jpg")
                        .photoLat(37.5668)
                        .photoLng(126.9783)
                        .distanceFromUser(2100.3)
                        .build()
        );
        
        given(courseService.getNearbyCourses(any(NearbyCoursesDTO.Request.class))).willReturn(mockCourses);
        
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
        this.mockMvc.perform(get("/api/courses/nearby")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .param("latitude", "37.5665")
                        .param("longitude", "126.9780")
                        .param("radius", "5000")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andDo(document("course-get-nearby",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("courses")
                                        .description("현재 위치 기반 코스 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .queryParameters(
                                                parameterWithName("latitude").description("현재 위치 위도"),
                                                parameterWithName("longitude").description("현재 위치 경도"),
                                                parameterWithName("radius").description("검색 반경 (미터, 기본값: 5000)").optional(),
                                                parameterWithName("limit").description("최대 결과 수 (기본값: 20)").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("코스 목록"),
                                                fieldWithPath("body[].id").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                fieldWithPath("body[].memberId").type(JsonFieldType.NUMBER).description("생성자 회원 ID"),
                                                fieldWithPath("body[].name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("body[].difficulty").type(JsonFieldType.STRING).description("난이도"),
                                                fieldWithPath("body[].points").type(JsonFieldType.ARRAY).description("코스 경로 좌표"),
                                                fieldWithPath("body[].points[].lat").type(JsonFieldType.NUMBER).description("위도"),
                                                fieldWithPath("body[].points[].lnt").type(JsonFieldType.NUMBER).description("경도"),
                                                fieldWithPath("body[].startLat").type(JsonFieldType.NUMBER).description("시작점 위도"),
                                                fieldWithPath("body[].startLng").type(JsonFieldType.NUMBER).description("시작점 경도"),
                                                fieldWithPath("body[].distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)"),
                                                fieldWithPath("body[].round").type(JsonFieldType.BOOLEAN).description("왕복 여부"),
                                                fieldWithPath("body[].region").type(JsonFieldType.STRING).description("지역"),
                                                fieldWithPath("body[].photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("body[].photoLat").type(JsonFieldType.NUMBER).description("사진 촬영 위치 위도").optional(),
                                                fieldWithPath("body[].photoLng").type(JsonFieldType.NUMBER).description("사진 촬영 위치 경도").optional(),
                                                fieldWithPath("body[].distanceFromUser").type(JsonFieldType.NUMBER).description("사용자로부터의 거리 (미터)")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getCourseDetailTest() throws Exception {
        // given
        Long courseId = 1L;
        CourseDetailDTO mockCourseDetail = CourseDetailDTO.builder()
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

        given(courseService.getCourseDetail(anyLong())).willReturn(mockCourseDetail);
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(get("/api/courses/{courseId}", courseId)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("course-get-detail",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("courses")
                                        .description("코스 상세 조회")
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
    void startRunningCourseTest() throws Exception {
        // given
        doNothing().when(courseService).startRunningCourse(anyLong(), anyLong());
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        this.mockMvc.perform(post("/api/courses/{courseId}/running", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("course-start-running",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("courses")
                                        .description("기존 코스로 러닝 시작")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("courseId").description("시작할 코스 ID")
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
    void finishRunningTest() throws Exception {
        // given
        doNothing().when(courseService).finishRunning(anyLong(), any(FinishRunning.class));
        given(jwtUtil.getMemberIdFromToken(anyString())).willReturn(1L);
        given(jwtUtil.getSocialIdFromToken(anyString())).willReturn("kakao_123");

        UserDetailsImpl mockUserDetails = UserDetailsImpl.builder()
                .memberId(1L)
                .socialId("kakao_123")
                .roles(List.of(MemberRole.USER))
                .build();
        given(userDetailsService.loadUserByUsername("1")).willReturn(mockUserDetails);

        // when
        Map<String, Object> finishRequest = createFinishRunningRequest();

        this.mockMvc.perform(post("/api/courses/finish")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(finishRequest)))
                .andExpect(status().isOk())
                .andDo(document("course-finish-running",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("courses")
                                        .description("러닝 종료 및 기록 저장")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
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

    private Map<String, Object> createFinishRunningRequest() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("distance", 5000.0);
        request.put("walk", 7200);
        request.put("calorie", 400);
        return request;
    }

}