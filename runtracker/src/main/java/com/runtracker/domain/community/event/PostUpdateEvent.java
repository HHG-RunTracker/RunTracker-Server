package com.runtracker.domain.community.event;

public record PostUpdateEvent(Long authorMemberId, Long postId) {
}