package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewRunning;
import com.runtracker.domain.crew.enums.CrewRunningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrewRunningRepository extends JpaRepository<CrewRunning, Long> {

    List<CrewRunning> findByCrewIdOrderByCreatedAtDesc(Long crewId);

    List<CrewRunning> findByCrewIdAndStatusOrderByCreatedAtDesc(Long crewId, CrewRunningStatus status);

    Optional<CrewRunning> findByIdAndCrewId(Long id, Long crewId);

    @Query("SELECT cr FROM CrewRunning cr WHERE cr.crewId = :crewId AND cr.status IN :statuses ORDER BY cr.createdAt DESC")
    List<CrewRunning> findByCrewIdAndStatusIn(@Param("crewId") Long crewId, @Param("statuses") List<CrewRunningStatus> statuses);

    @Query("SELECT cr FROM CrewRunning cr WHERE cr.crewId = :crewId AND cr.createdAt >= :startDate AND cr.createdAt <= :endDate ORDER BY cr.createdAt DESC")
    List<CrewRunning> findByCrewIdAndDateRange(@Param("crewId") Long crewId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    long countByCrewIdAndStatus(Long crewId, CrewRunningStatus status);

    boolean existsByCrewIdAndStatus(Long crewId, CrewRunningStatus status);
}