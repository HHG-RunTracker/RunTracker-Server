package com.runtracker.domain.crew.dto;

import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.member.entity.enums.MemberRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CrewManagementDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String name;
        private Integer age;
        private Boolean gender;
        private MemberRole role;
        private CrewMemberStatus status;
        private LocalDateTime requestedAt;

        public static MemberInfo from(CrewMember crewMember, String name, Integer age, Boolean gender) {
            return MemberInfo.builder()
                    .memberId(crewMember.getMemberId())
                    .name(name)
                    .age(age)
                    .gender(gender)
                    .role(crewMember.getRole())
                    .status(crewMember.getStatus())
                    .requestedAt(crewMember.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PendingMembersResponse {
        private List<MemberInfo> pendingMembers;
        private Integer totalCount;

        public static PendingMembersResponse of(List<MemberInfo> pendingMembers) {
            return PendingMembersResponse.builder()
                    .pendingMembers(pendingMembers)
                    .totalCount(pendingMembers.size())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BannedMembersResponse {
        private List<MemberInfo> bannedMembers;
        private Integer totalCount;

        public static BannedMembersResponse of(List<MemberInfo> bannedMembers) {
            return BannedMembersResponse.builder()
                    .bannedMembers(bannedMembers)
                    .totalCount(bannedMembers.size())
                    .build();
        }
    }
}