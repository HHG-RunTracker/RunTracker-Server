package com.runtracker.domain.crew.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.member.entity.enums.MemberRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CrewDetailDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private MemberRole role;
        private CrewMemberStatus status;
        private LocalDateTime joinedAt;

        public static MemberInfo from(CrewMember crewMember) {
            return MemberInfo.builder()
                    .memberId(crewMember.getMemberId())
                    .role(crewMember.getRole())
                    .status(crewMember.getStatus())
                    .joinedAt(crewMember.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String photo;
        private String introduce;
        private String region;
        private Difficulty difficulty;
        private String schedules;
        private Long leaderId;
        private Integer totalMemberCount;
        private Integer activeMemberCount;
        private List<MemberInfo> members;
        private LocalDateTime createdAt;

        public static Response from(Crew crew, List<CrewMember> allMembers) {
            List<CrewMember> activeMembers = allMembers.stream()
                    .filter(member -> member.getStatus() == CrewMemberStatus.ACTIVE)
                    .toList();

            List<MemberInfo> memberInfos = activeMembers.stream()
                    .map(MemberInfo::from)
                    .toList();

            return Response.builder()
                    .id(crew.getId())
                    .title(crew.getTitle())
                    .photo(crew.getPhoto())
                    .introduce(crew.getIntroduce())
                    .region(crew.getRegion())
                    .difficulty(crew.getDifficulty())
                    .schedules(crew.getSchedules())
                    .leaderId(crew.getLeaderId())
                    .totalMemberCount(allMembers.size())
                    .activeMemberCount(activeMembers.size())
                    .members(memberInfos)
                    .createdAt(crew.getCreatedAt())
                    .build();
        }
    }
}