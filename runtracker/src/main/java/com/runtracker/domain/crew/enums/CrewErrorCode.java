package com.runtracker.domain.crew.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewErrorCode implements ResponseCode {

    MEMBER_NOT_FOUND("CR001", "멤버를 찾을 수 없습니다."),
    CREW_ALREADY_EXISTS("CR002", "이미 생성한 크루가 존재합니다."),
    CREW_NOT_FOUND("CR003", "크루를 찾을 수 없습니다."),
    CREW_MEMBER_NOT_FOUND("CR004", "크루 멤버를 찾을 수 없습니다."),
    ALREADY_CREW_MEMBER("CR005", "이미 크루에 가입된 멤버입니다."),
    CREW_APPLICATION_PENDING("CR006", "이미 크루 가입 신청이 진행 중입니다.");

    private final String statusCode;
    private final String message;
}