package com.runtracker.domain.community.controller;

import com.runtracker.domain.community.dto.PostCreateDTO;
import com.runtracker.domain.community.dto.PostUpdateDTO;
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
            @RequestBody PostCreateDTO postCreateDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.createPost(crewId, postCreateDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/crews/{crewId}/posts/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @RequestBody PostUpdateDTO postUpdateDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        postService.updatePost(postId, postUpdateDTO, userDetails);
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
}