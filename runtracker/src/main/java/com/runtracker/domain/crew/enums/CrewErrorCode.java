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
    CREW_APPLICATION_PENDING("CR006", "이미 크루 가입 신청이 진행 중입니다."),
    NO_PENDING_APPLICATION("CR007", "취소할 가입 신청이 없습니다."),
    NOT_CREW_LEADER("CR008", "크루 관리 권한이 없습니다."),
    APPLICANT_NOT_FOUND("CR009", "해당 가입 신청자를 찾을 수 없습니다."),
    CANNOT_MODIFY_LEADER_ROLE("CR010", "크루장의 권한은 변경할 수 없습니다."),
    SAME_ROLE_UPDATE("CR011", "이미 동일한 권한을 가지고 있습니다."),
    INVALID_CREW_ROLE("CR012", "크루에서 사용할 수 없는 권한입니다."),
    ALREADY_JOINED_OTHER_CREW("CR013", "이미 다른 크루에 가입되어 있습니다."),
    CANNOT_KICK_CREW_LEADER("CR014", "크루장은 추방할 수 없습니다."),
    CANNOT_KICK_YOURSELF("CR015", "자기 자신을 추방할 수 없습니다."),
    CANNOT_KICK_MANAGER_AS_MANAGER("CR016", "매니저는 다른 매니저를 추방할 수 없습니다."),
    BANNED_FROM_CREW("CR017", "해당 크루에서 차단된 회원입니다."),
    CANNOT_LEAVE_AS_CREW_LEADER("CR018", "크루장은 크루를 나갈 수 없습니다."),
    CREW_RUNNING_NOT_FOUND("CR019", "크루 런닝방을 찾을 수 없습니다."),
    CREW_RUNNING_NOT_WAITING("CR020", "참여할 수 없는 크루 런닝 상태입니다."),
    ALREADY_JOINED_CREW_RUNNING("CR021", "이미 참여한 크루 런닝입니다."),
    CREW_RUNNING_CREATION_FAILED("CR022", "크루 런닝 생성에 실패했습니다."),
    CREW_RUNNING_JOIN_FAILED("CR023", "크루 런닝 참여에 실패했습니다."),
    NOT_JOINED_CREW_RUNNING("CR024", "참여하지 않은 크루 런닝입니다."),
    CANNOT_LEAVE_STARTED_RUNNING("CR025", "이미 시작된 크루 런닝은 나갈 수 없습니다."),
    CANNOT_DELETE_STARTED_RUNNING("CR026", "이미 시작된 크루 런닝은 삭제할 수 없습니다.");

    private final String statusCode;
    private final String message;
}