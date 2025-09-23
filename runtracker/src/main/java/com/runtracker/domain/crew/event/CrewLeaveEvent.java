package com.runtracker.domain.crew.event;

import java.util.List;

public record CrewLeaveEvent(List<Long> managerIds, String leavingMemberName) {
}