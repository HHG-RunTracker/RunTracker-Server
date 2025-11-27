package com.runtracker.domain.community.event;

public record PostCommentEvent(Long commenterMemberId, Long postAuthorMemberId, Long postId) {
}