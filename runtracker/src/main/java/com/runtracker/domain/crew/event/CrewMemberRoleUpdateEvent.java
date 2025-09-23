package com.runtracker.domain.crew.event;

import com.runtracker.domain.member.entity.enums.MemberRole;

public record CrewMemberRoleUpdateEvent(Long targetMemberId, Long crewId, MemberRole newRole) {
}