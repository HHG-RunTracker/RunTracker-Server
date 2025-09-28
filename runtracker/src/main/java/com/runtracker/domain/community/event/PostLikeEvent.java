package com.runtracker.domain.community.event;

public record PostLikeEvent(Long likerMemberId, Long postAuthorMemberId, Long postId) {
}