package com.runtracker.domain.crew.event;

public record CrewJoinRequestApprovalEvent(Long approvedUserId, Long crewId, boolean isApproved) {
}