package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.member.entity.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);
}