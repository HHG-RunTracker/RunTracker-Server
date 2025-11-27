package com.runtracker.domain.crew.event;

import java.util.List;

public record CrewDeleteEvent(List<Long> memberIds, String crewTitle) {
}