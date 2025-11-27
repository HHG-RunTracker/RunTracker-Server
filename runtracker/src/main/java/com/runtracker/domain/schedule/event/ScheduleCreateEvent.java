package com.runtracker.domain.schedule.event;

public record ScheduleCreateEvent(Long creatorId, Long crewId, String scheduleTitle) {
}