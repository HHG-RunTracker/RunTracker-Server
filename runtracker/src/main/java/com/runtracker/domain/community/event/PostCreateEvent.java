package com.runtracker.domain.community.event;

public record PostCreateEvent(Long authorMemberId, Long postId) {
}