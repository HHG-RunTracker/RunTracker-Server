package com.runtracker.global.security;

import com.runtracker.domain.crew.exception.NotCrewLeaderException;
import com.runtracker.domain.crew.exception.UnauthorizedCrewAccessException;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.global.security.dto.CrewMembership;
import org.springframework.stereotype.Component;

@Component
public class CrewAuthorizationUtil {

    /**
     * 크루장 권한 검증
     */
    public void validateCrewLeaderPermission(UserDetailsImpl userDetails, Long crewId) {
        CrewMembership membership = userDetails.getCrewMembership();
        
        if (membership == null) {
            throw new NotCrewLeaderException();
        }
        
        if (!membership.getCrewId().equals(crewId)) {
            throw new NotCrewLeaderException();
        }
        
        if (membership.getRole() != MemberRole.CREW_LEADER) {
            throw new NotCrewLeaderException();
        }
    }

    /**
     * 크루 관리 권한 검증
     */
    public void validateCrewManagementPermission(UserDetailsImpl userDetails, Long crewId) {
        CrewMembership membership = userDetails.getCrewMembership();
        
        if (membership == null) {
            throw new NotCrewLeaderException();
        }
        
        if (!membership.getCrewId().equals(crewId)) {
            throw new NotCrewLeaderException();
        }
        
        if (membership.getRole() != MemberRole.CREW_LEADER && 
            membership.getRole() != MemberRole.CREW_MANAGER) {
            throw new NotCrewLeaderException();
        }
    }

    /**
     * 크루 멤버 접근 권한 검증
     */
    public void validateCrewMemberAccess(UserDetailsImpl userDetails, Long crewId) {
        CrewMembership membership = userDetails.getCrewMembership();
        
        if (membership == null) {
            throw new UnauthorizedCrewAccessException();
        }
        
        if (!membership.getCrewId().equals(crewId)) {
            throw new UnauthorizedCrewAccessException();
        }
    }
}