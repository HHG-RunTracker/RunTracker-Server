package com.runtracker.domain.crew.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewErrorCode implements ResponseCode {

    MEMBER_NOT_FOUND("CR001", "Member not found"),
    CREW_ALREADY_EXISTS("CR002", "Crew already exists"),
    CREW_NOT_FOUND("CR003", "Crew not found"),
    CREW_MEMBER_NOT_FOUND("CR004", "Crew member not found"),
    ALREADY_CREW_MEMBER("CR005", "Already a crew member"),
    CREW_APPLICATION_PENDING("CR006", "Crew application is pending"),
    NO_PENDING_APPLICATION("CR007", "No pending application to cancel"),
    NOT_CREW_LEADER("CR008", "No crew management permission"),
    APPLICANT_NOT_FOUND("CR009", "Applicant not found"),
    CANNOT_MODIFY_LEADER_ROLE("CR010", "Cannot modify leader role"),
    SAME_ROLE_UPDATE("CR011", "Already has the same role"),
    INVALID_CREW_ROLE("CR012", "Invalid crew role"),
    ALREADY_JOINED_OTHER_CREW("CR013", "Already joined another crew"),
    CANNOT_KICK_CREW_LEADER("CR014", "Cannot kick crew leader"),
    CANNOT_KICK_YOURSELF("CR015", "Cannot kick yourself"),
    CANNOT_KICK_MANAGER_AS_MANAGER("CR016", "Manager cannot kick another manager"),
    BANNED_FROM_CREW("CR017", "Banned from crew"),
    CANNOT_LEAVE_AS_CREW_LEADER("CR018", "Crew leader cannot leave crew"),
    UNAUTHORIZED_CREW_ACCESS("CR019", "Unauthorized crew access");

    private final String statusCode;
    private final String message;
}