package com.runtracker.domain.schedule.event;

public record ScheduleCancelEvent(Long participantId, Long crewId, String scheduleTitle) {
}