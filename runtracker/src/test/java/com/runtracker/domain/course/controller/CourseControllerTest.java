package com.runtracker.domain.course.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.course.dto.CourseDTO;
import com.runtracker.domain.course.service.CourseService;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.enums.MemberRole;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private CourseService courseService;

    @Test
    void createFreeRunningCourseTest() throws Exception {
        // given
        doNothing().when(courseService).createFreeRunningCourse(any(CourseDTO.class));
        
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
        Map<String, Object> request = createCourseRequest();

        this.mockMvc.perform(post("/api/courses/free-running")
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andDo(document("course-create-free-running",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("courses")
                                        .description("자유 러닝 코스 생성")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("엑세스 토큰")
                                        )
                                        .requestFields(
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("코스 이름"),
                                                fieldWithPath("difficulty").type(JsonFieldType.STRING).description("난이도 (EASY, MEDIUM, HARD)"),
                                                fieldWithPath("points").type(JsonFieldType.ARRAY).description("코스 경로 좌표 리스트"),
                                                fieldWithPath("points[].lat").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                                fieldWithPath("points[].lnt").type(JsonFieldType.NUMBER).description("좌표 경도"),
                                                fieldWithPath("startLat").type(JsonFieldType.NUMBER).description("시작 지점 위도").optional(),
                                                fieldWithPath("startLng").type(JsonFieldType.NUMBER).description("시작 지점 경도").optional(),
                                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("코스 거리 (미터)").optional(),
                                                fieldWithPath("round").type(JsonFieldType.BOOLEAN).description("왕복 여부").optional(),
                                                fieldWithPath("indexs").type(JsonFieldType.STRING).description("인덱스 정보 (JSON 문자열)").optional(),
                                                fieldWithPath("region").type(JsonFieldType.STRING).description("코스 지역").optional(),
                                                fieldWithPath("photo").type(JsonFieldType.STRING).description("코스 사진 URL").optional(),
                                                fieldWithPath("photoLat").type(JsonFieldType.NUMBER).description("사진 촬영 위치 위도").optional(),
                                                fieldWithPath("photoLng").type(JsonFieldType.NUMBER).description("사진 촬영 위치 경도").optional()
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
        request.put("difficulty", "EASY");

        Map<String, Object> point1 = new LinkedHashMap<>();
        point1.put("lat", 37.5512);
        point1.put("lnt", 126.9882);
        
        Map<String, Object> point2 = new LinkedHashMap<>();
        point2.put("lat", 37.5523);
        point2.put("lnt", 126.9891);
        
        request.put("points", List.of(point1, point2));

        request.put("startLat", 37.5665);
        request.put("startLng", 126.9780);
        request.put("distance", 5000.0);
        request.put("round", false);
        request.put("indexs", "[0,1,2]");
        request.put("region", "서울특별시 중구");
        request.put("photo", "https://example.com/photo.jpg");
        request.put("photoLat", 37.5670);
        request.put("photoLng", 126.9785);
        
        return request;
    }
}