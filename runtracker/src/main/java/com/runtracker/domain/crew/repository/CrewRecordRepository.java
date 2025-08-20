package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.CrewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrewRecordRepository extends JpaRepository<CrewRecord, Long> {
    
    Optional<CrewRecord> findByCrewRunningId(Long crewRunningId);
}