package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewRunningParticipant;
import com.runtracker.domain.crew.enums.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewRunningParticipantRepository extends JpaRepository<CrewRunningParticipant, Long> {

    List<CrewRunningParticipant> findByCrewRunningIdOrderByJoinedAtAsc(Long crewRunningId);

    List<CrewRunningParticipant> findByCrewRunningIdAndStatusOrderByJoinedAtAsc(Long crewRunningId, ParticipantStatus status);

    Optional<CrewRunningParticipant> findByCrewRunningIdAndMemberId(Long crewRunningId, Long memberId);

    List<CrewRunningParticipant> findByMemberIdOrderByJoinedAtDesc(Long memberId);

    @Query("SELECT crp FROM CrewRunningParticipant crp WHERE crp.crewRunningId IN :crewRunningIds ORDER BY crp.joinedAt ASC")
    List<CrewRunningParticipant> findByCrewRunningIds(@Param("crewRunningIds") List<Long> crewRunningIds);

    long countByCrewRunningIdAndStatus(Long crewRunningId, ParticipantStatus status);

    boolean existsByCrewRunningIdAndMemberId(Long crewRunningId, Long memberId);

    @Query("SELECT COUNT(crp) FROM CrewRunningParticipant crp WHERE crp.crewRunningId = :crewRunningId")
    long countParticipantsByCrewRunningId(@Param("crewRunningId") Long crewRunningId);

    List<CrewRunningParticipant> findByMemberIdAndStatusIn(Long memberId, List<ParticipantStatus> statuses);
}