package com.runtracker.domain.crew.event;

public record CrewJoinRequestCancelEvent(Long canceledUserId, Long managerId, Long crewId) {
}