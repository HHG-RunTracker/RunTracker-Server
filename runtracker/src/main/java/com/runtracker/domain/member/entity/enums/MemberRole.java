package com.runtracker.domain.member.entity.enums;

public enum MemberRole {
    USER("일반 사용자"),
    CREW_LEADER("크루장"),
    CREW_MANAGER("크루 매니저"),
    CREW_MEMBER("크루원"),
    ADMIN("관리자");
    
    private final String description;
    
    MemberRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}