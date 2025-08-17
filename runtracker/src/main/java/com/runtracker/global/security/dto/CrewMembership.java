package com.runtracker.global.security.dto;

import com.runtracker.domain.member.entity.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewMembership {
    private Long crewId;
    private MemberRole role;
}