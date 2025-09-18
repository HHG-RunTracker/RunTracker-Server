package com.runtracker.domain.community.controller;

import com.runtracker.domain.community.dto.CommentDTO;
import com.runtracker.domain.community.dto.PostDTO;
import com.runtracker.domain.community.dto.PostDetailDTO;
import com.runtracker.domain.community.dto.PostListDTO;

import java.util.List;

import com.runtracker.domain.community.service.CommunityService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/posts")
    public ApiResponse<Void> createPost(
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.createPost(postDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/posts/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.updatePost(postId, postDTO, userDetails);
        return ApiResponse.ok();
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.deletePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/posts/{postId}/like")
    public ApiResponse<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.likePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/posts/{postId}/unlike")
    public ApiResponse<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.unlikePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<Void> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.createComment(postId, commentDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.updateComment(commentId, commentDTO, userDetails);
        return ApiResponse.ok();
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        communityService.deleteComment(commentId, userDetails);
        return ApiResponse.ok();
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostListDTO>> getPostList(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<PostListDTO> posts = communityService.getPostList(userDetails);
        return ApiResponse.ok(posts);
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<PostDetailDTO> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PostDetailDTO post = communityService.getPostDetail(postId, userDetails);
        return ApiResponse.ok(post);
    }

    @GetMapping("/posts/search")
    public ApiResponse<List<PostListDTO>> searchPosts(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<PostListDTO> posts = communityService.searchPosts(keyword, userDetails);
        return ApiResponse.ok(posts);
    }
}