package com.runtracker.domain.schedule.event;

public record ScheduleJoinEvent(Long participantId, Long crewId, String scheduleTitle) {
}