package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrewRankingRepository extends JpaRepository<CrewRanking, Long> {
    
    List<CrewRanking> findByDateOrderByRankPosition(LocalDate date);
    
    Optional<CrewRanking> findByDateAndCrewId(LocalDate date, Long crewId);
    
    List<CrewRanking> findByCrewIdOrderByDateDesc(Long crewId);
    
    @Query("SELECT cr FROM CrewRanking cr WHERE cr.date = :date AND cr.rankPosition <= :topN ORDER BY cr.rankPosition")
    List<CrewRanking> findTopNByDate(@Param("date") LocalDate date, @Param("topN") int topN);
    
    List<CrewRanking> findByDateOrderByTotalDistanceDesc(LocalDate date);
    
    void deleteByDate(LocalDate date);
}