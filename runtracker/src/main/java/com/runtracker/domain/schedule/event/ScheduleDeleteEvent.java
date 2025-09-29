package com.runtracker.domain.schedule.event;

public record ScheduleDeleteEvent(Long deleterId, Long crewId, String scheduleTitle) {
}