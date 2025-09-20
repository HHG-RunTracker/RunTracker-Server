package com.runtracker.domain.crew.event;

public record CrewJoinRequestEvent(Long requestUserId, Long managerId, Long crewId) {
}