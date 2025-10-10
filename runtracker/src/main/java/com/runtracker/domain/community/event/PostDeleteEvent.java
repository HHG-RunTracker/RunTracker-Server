package com.runtracker.domain.community.event;

public record PostDeleteEvent(Long authorMemberId, Long postId) {
}