package com.runtracker.domain.crew.entity;

import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crew_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrewMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private CrewMemberStatus status = CrewMemberStatus.ACTIVE;
    
    public void approve() {
        this.status = CrewMemberStatus.ACTIVE;
    }
}