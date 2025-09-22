package com.runtracker.domain.crew.event;

public record CrewBanEvent(Long bannedMemberId, String crewTitle) {
}