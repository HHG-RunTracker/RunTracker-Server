package com.runtracker.domain.community.controller;

import com.runtracker.domain.community.dto.CommentDTO;
import com.runtracker.domain.community.dto.PostDTO;
import com.runtracker.domain.community.service.PostService;
import com.runtracker.global.response.ApiResponse;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/crews/{crewId}/posts")
    public ApiResponse<Void> createPost(
            @PathVariable Long crewId,
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.createPost(crewId, postDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/crews/{crewId}/posts/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.updatePost(postId, postDTO, userDetails);
        return ApiResponse.ok();
    }

    @DeleteMapping("/crews/{crewId}/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.deletePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/crews/{crewId}/posts/{postId}/like")
    public ApiResponse<Void> likePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.likePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/crews/{crewId}/posts/{postId}/unlike")
    public ApiResponse<Void> unlikePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.unlikePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/crews/{crewId}/posts/{postId}/comments")
    public ApiResponse<Void> createComment(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.createComment(postId, commentDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/crews/{crewId}/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.updateComment(commentId, commentDTO, userDetails);
        return ApiResponse.ok();
    }

    @DeleteMapping("/crews/{crewId}/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.deleteComment(commentId, userDetails);
        return ApiResponse.ok();
    }
}