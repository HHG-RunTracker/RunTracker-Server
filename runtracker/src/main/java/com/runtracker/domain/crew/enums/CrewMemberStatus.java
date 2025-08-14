package com.runtracker.domain.crew.enums;

public enum CrewMemberStatus {
    ACTIVE("활성화"),
    PENDING("승인 대기"),
    BANNED("차단"),
    WITHDRAWN("탈퇴");

    private final String description;

    CrewMemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}