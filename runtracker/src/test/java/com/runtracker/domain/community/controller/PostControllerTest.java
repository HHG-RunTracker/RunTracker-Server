package com.runtracker.domain.community.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.community.dto.PostCreateDTO;
import com.runtracker.domain.community.service.PostService;
import com.runtracker.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private PostService postService;

    @Test
    void createPost() throws Exception {
        // given
        doNothing().when(postService).createPost(anyLong(), any(PostCreateDTO.class), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(post("/api/community/crews/{crewId}/posts", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(PostCreateDTO.builder()
                                .title("오늘의 러닝 후기")
                                .content("오늘 5km 완주했어요! 날씨가 러닝하기 딱 좋았습니다. 다음엔 더 먼 거리에 도전해보려고요.")
                                .photos(List.of("https://example.com/running1.jpg", "https://example.com/running2.jpg"))
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("community-post-create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .description("크루 커뮤니티 게시글 작성")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목").optional(),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용").optional(),
                                                fieldWithPath("photos").type(JsonFieldType.ARRAY).description("첨부 사진 URL 배열").optional()
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