package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.member.entity.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);
    List<CrewMember> findByCrewId(Long crewId);
    List<CrewMember> findByMemberIdAndStatus(Long memberId, CrewMemberStatus status);

    @Query("SELECT cm.memberId FROM CrewMember cm WHERE cm.crewId = :crewId AND cm.status != 'BANNED'")
    List<Long> findMemberIdsByCrewId(@Param("crewId") Long crewId);
}