package com.runtracker.domain.crew.repository;

import com.runtracker.domain.crew.entity.Crew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {
    List<Crew> findByLeaderId(Long leaderId);
}