package com.runtracker.domain.community.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.community.dto.CommentDTO;
import com.runtracker.domain.community.dto.CommentInfoDTO;
import com.runtracker.domain.community.dto.PostDTO;
import com.runtracker.domain.community.dto.PostDetailDTO;
import com.runtracker.domain.community.dto.PostListDTO;
import com.runtracker.domain.community.service.PostService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private PostService postService;

    @Test
    void createPost() throws Exception {
        // given
        doNothing().when(postService).createPost(anyLong(), any(PostDTO.class), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(post("/api/community/crews/{crewId}/posts", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(PostDTO.builder()
                                .title("오늘의 러닝 후기")
                                .content("오늘 5km 완주했어요! 날씨가 러닝하기 딱 좋았습니다. 다음엔 더 먼 거리에 도전해보려고요.")
                                .photos(List.of("https://example.com/running1.jpg", "https://example.com/running2.jpg"))
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("community-post-create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 작성")
                                        .description("크루 멤버가 해당 크루의 커뮤니티에 새로운 게시글을 작성합니다. 제목과 내용은 필수이고 사진은 선택사항입니다.")
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

    @Test
    void updatePost() throws Exception {
        // given
        doNothing().when(postService).updatePost(anyLong(), any(PostDTO.class), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(patch("/api/community/crews/{crewId}/posts/{postId}", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(PostDTO.builder()
                                .title("수정된 러닝 후기")
                                .content("오늘 10km 완주했어요! 목표를 달성해서 기분이 좋습니다.")
                                .photos(List.of("https://example.com/updated1.jpg"))
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("community-post-update",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 수정")
                                        .description("작성자만 본인이 작성한 게시글을 수정할 수 있습니다. 수정하지 않는 필드는 요청에서 제외해도 됩니다.")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("수정할 게시글 ID")
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

    @Test
    void deletePost() throws Exception {
        // given
        doNothing().when(postService).deletePost(anyLong(), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(delete("/api/community/crews/{crewId}/posts/{postId}", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("community-post-delete",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 삭제")
                                        .description("크루 커뮤니티 게시글 삭제")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("삭제할 게시글 ID")
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
    void likePost() throws Exception {
        // given
        doNothing().when(postService).likePost(anyLong(), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(post("/api/community/crews/{crewId}/posts/{postId}/like", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("community-post-like",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 좋아요")
                                        .description("크루 멤버가 해당 크루의 게시글에 좋아요를 표시합니다. 이미 좋아요한 게시글에는 중복으로 좋아요할 수 없습니다.")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("좋아요할 게시글 ID")
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
    void unlikePost() throws Exception {
        // given
        doNothing().when(postService).unlikePost(anyLong(), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(post("/api/community/crews/{crewId}/posts/{postId}/unlike", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("community-post-unlike",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 좋아요 취소")
                                        .description("크루 멤버가 이전에 좋아요한 게시글의 좋아요를 취소합니다. 좋아요하지 않은 게시글은 취소할 수 없습니다.")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("좋아요 취소할 게시글 ID")
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
    void createComment() throws Exception {
        // given
        doNothing().when(postService).createComment(anyLong(), any(CommentDTO.class), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(post("/api/community/crews/{crewId}/posts/{postId}/comments", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(CommentDTO.builder()
                                .comment("정말 대단하세요! 저도 5km 도전해봐야겠어요.")
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("community-comment-create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 댓글 작성")
                                        .description("크루 멤버가 해당 크루의 게시글에 댓글을 작성합니다.")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("게시글 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용")
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
    void updateComment() throws Exception {
        // given
        doNothing().when(postService).updateComment(anyLong(), any(CommentDTO.class), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(patch("/api/community/crews/{crewId}/posts/{postId}/comments/{commentId}", 1L, 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN)
                        .contentType("application/json")
                        .content(toJson(CommentDTO.builder()
                                .comment("수정된 댓글입니다. 정말 멋진 러닝이었네요!")
                                .build())))
                .andExpect(status().isOk())
                .andDo(document("community-comment-update",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 댓글 수정")
                                        .description("크루 커뮤니티 댓글 수정")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("게시글 ID"),
                                                parameterWithName("commentId").description("수정할 댓글 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용").optional()
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
    void deleteComment() throws Exception {
        // given
        doNothing().when(postService).deleteComment(anyLong(), any(UserDetailsImpl.class));

        // when
        this.mockMvc.perform(delete("/api/community/crews/{crewId}/posts/{postId}/comments/{commentId}", 1L, 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("community-comment-delete",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 댓글 삭제")
                                        .description("크루 커뮤니티 댓글 삭제")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("게시글 ID"),
                                                parameterWithName("commentId").description("삭제할 댓글 ID")
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
    void getPostList() throws Exception {
        // given
        List<PostListDTO> mockPosts = List.of(
                PostListDTO.builder()
                        .postId(1L)
                        .title("오늘의 러닝 후기")
                        .content("오늘 5km 완주했어요! 날씨가 러닝하기 딱 좋았습니다.")
                        .photos(List.of("https://example.com/running1.jpg", "https://example.com/running2.jpg"))
                        .memberId(1L)
                        .memberName("김러너")
                        .likeCount(15L)
                        .commentCount(3L)
                        .isLiked(true)
                        .createdAt(LocalDateTime.of(2024, 12, 25, 14, 30))
                        .build(),
                PostListDTO.builder()
                        .postId(2L)
                        .title("새벽 러닝 도전")
                        .content("새벽 5시에 나가서 10km 달렸어요. 상쾌한 기분입니다!")
                        .photos(List.of())
                        .memberId(2L)
                        .memberName("박러너")
                        .likeCount(8L)
                        .commentCount(1L)
                        .isLiked(false)
                        .createdAt(LocalDateTime.of(2024, 12, 25, 10, 15))
                        .build()
        );
        when(postService.getPostList(anyLong(), any(UserDetailsImpl.class))).thenReturn(mockPosts);

        // when
        this.mockMvc.perform(get("/api/community/crews/{crewId}/posts", 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("community-post-list",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 목록 조회")
                                        .description("해당 크루의 모든 게시글을 최신순으로 조회합니다. 각 게시글의 기본 정보와 함께 좋아요 수, 댓글 수, 현재 사용자의 좋아요 여부 (isLiked가 true -> 좋아요 누름, false -> 좋아요 안누름)")
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
                                                fieldWithPath("body").type(JsonFieldType.ARRAY).description("게시글 목록"),
                                                fieldWithPath("body[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                                fieldWithPath("body[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                                fieldWithPath("body[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                                fieldWithPath("body[].photos").type(JsonFieldType.ARRAY).description("첨부 사진 URL 배열"),
                                                fieldWithPath("body[].memberId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                                fieldWithPath("body[].memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                                                fieldWithPath("body[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                fieldWithPath("body[].commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                                fieldWithPath("body[].isLiked").type(JsonFieldType.BOOLEAN).description("현재 사용자의 좋아요 여부"),
                                                fieldWithPath("body[].createdAt").type(JsonFieldType.STRING).description("작성일시")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getPostDetail() throws Exception {
        // given
        PostDetailDTO mockPost = PostDetailDTO.builder()
                .postId(1L)
                .title("오늘의 러닝 후기")
                .content("오늘 5km 완주했어요! 날씨가 러닝하기 딱 좋았습니다. 다음엔 더 먼 거리에 도전해보려고요.")
                .photos(List.of("https://example.com/running1.jpg", "https://example.com/running2.jpg"))
                .memberId(1L)
                .memberName("김러너")
                .likeCount(15L)
                .isLiked(true)
                .createdAt(LocalDateTime.of(2024, 12, 25, 14, 30))
                .updatedAt(LocalDateTime.of(2024, 12, 25, 15, 0))
                .comments(List.of(
                        CommentInfoDTO.builder()
                                .commentId(1L)
                                .comment("정말 대단하세요! 저도 5km 도전해봐야겠어요.")
                                .memberId(2L)
                                .memberName("박러너")
                                .createdAt(LocalDateTime.of(2024, 12, 25, 14, 45))
                                .updatedAt(LocalDateTime.of(2024, 12, 25, 14, 45))
                                .build(),
                        CommentInfoDTO.builder()
                                .commentId(2L)
                                .comment("화이팅! 다음엔 같이 뛰어요!")
                                .memberId(3L)
                                .memberName("최러너")
                                .createdAt(LocalDateTime.of(2024, 12, 25, 15, 10))
                                .updatedAt(LocalDateTime.of(2024, 12, 25, 15, 10))
                                .build()
                ))
                .build();
        when(postService.getPostDetail(anyLong(), anyLong(), any(UserDetailsImpl.class))).thenReturn(mockPost);

        // when
        this.mockMvc.perform(get("/api/community/crews/{crewId}/posts/{postId}", 1L, 1L)
                        .header(AUTH_HEADER, TEST_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("community-post-detail",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("크루 커뮤니티 게시글 상세 조회")
                                        .description("특정 게시글의 상세 정보를 조회합니다. 게시글 내용과 함께 모든 댓글 목록을 포함.")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("액세스 토큰")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루 ID"),
                                                parameterWithName("postId").description("조회할 게시글 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body").type(JsonFieldType.OBJECT).description("게시글 상세 정보"),
                                                fieldWithPath("body.postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                                fieldWithPath("body.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                                fieldWithPath("body.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                                fieldWithPath("body.photos").type(JsonFieldType.ARRAY).description("첨부 사진 URL 배열"),
                                                fieldWithPath("body.memberId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                                fieldWithPath("body.memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                                                fieldWithPath("body.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                fieldWithPath("body.isLiked").type(JsonFieldType.BOOLEAN).description("현재 사용자의 좋아요 여부"),
                                                fieldWithPath("body.createdAt").type(JsonFieldType.STRING).description("작성일시"),
                                                fieldWithPath("body.updatedAt").type(JsonFieldType.STRING).description("수정일시"),
                                                fieldWithPath("body.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                                                fieldWithPath("body.comments[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                                fieldWithPath("body.comments[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                                fieldWithPath("body.comments[].memberId").type(JsonFieldType.NUMBER).description("댓글 작성자 ID"),
                                                fieldWithPath("body.comments[].memberName").type(JsonFieldType.STRING).description("댓글 작성자 이름"),
                                                fieldWithPath("body.comments[].createdAt").type(JsonFieldType.STRING).description("댓글 작성일시"),
                                                fieldWithPath("body.comments[].updatedAt").type(JsonFieldType.STRING).description("댓글 수정일시")
                                        )
                                        .build()
                        )
                ));
    }
}