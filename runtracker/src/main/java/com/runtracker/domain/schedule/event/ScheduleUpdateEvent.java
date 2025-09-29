package com.runtracker.domain.schedule.event;

public record ScheduleUpdateEvent(Long updaterId, Long crewId, String scheduleTitle) {
}