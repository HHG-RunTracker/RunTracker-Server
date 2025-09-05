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

    @PostMapping("/crews/{crewId}/posts")
    public ApiResponse<Void> createPost(
            @PathVariable Long crewId,
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.createPost(crewId, postDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/crews/{crewId}/posts/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.updatePost(postId, postDTO, userDetails);
        return ApiResponse.ok();
    }

    @DeleteMapping("/crews/{crewId}/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.deletePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/crews/{crewId}/posts/{postId}/like")
    public ApiResponse<Void> likePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.likePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/crews/{crewId}/posts/{postId}/unlike")
    public ApiResponse<Void> unlikePost(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.unlikePost(postId, userDetails);
        return ApiResponse.ok();
    }

    @PostMapping("/crews/{crewId}/posts/{postId}/comments")
    public ApiResponse<Void> createComment(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.createComment(postId, commentDTO, userDetails);
        return ApiResponse.ok();
    }

    @PatchMapping("/crews/{crewId}/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.updateComment(commentId, commentDTO, userDetails);
        return ApiResponse.ok();
    }

    @DeleteMapping("/crews/{crewId}/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        communityService.deleteComment(commentId, userDetails);
        return ApiResponse.ok();
    }

    @GetMapping("/crews/{crewId}/posts")
    public ApiResponse<List<PostListDTO>> getPostList(
            @PathVariable Long crewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<PostListDTO> posts = communityService.getPostList(crewId, userDetails);
        return ApiResponse.ok(posts);
    }

    @GetMapping("/crews/{crewId}/posts/{postId}")
    public ApiResponse<PostDetailDTO> getPostDetail(
            @PathVariable Long crewId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        PostDetailDTO post = communityService.getPostDetail(crewId, postId, userDetails);
        return ApiResponse.ok(post);
    }

    @GetMapping("/crews/{crewId}/posts/search")
    public ApiResponse<List<PostListDTO>> searchPosts(
            @PathVariable Long crewId,
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<PostListDTO> posts = communityService.searchPosts(crewId, keyword, userDetails);
        return ApiResponse.ok(posts);
    }
}