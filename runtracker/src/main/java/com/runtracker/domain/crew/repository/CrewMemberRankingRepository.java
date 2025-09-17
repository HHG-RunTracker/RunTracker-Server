package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewMemberRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrewMemberRankingRepository extends JpaRepository<CrewMemberRanking, Long> {
    List<CrewMemberRanking> findByCrewIdAndDateOrderByRankPosition(Long crewId, LocalDate date);
}