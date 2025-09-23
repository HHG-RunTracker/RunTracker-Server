package com.runtracker.domain.schedule.repository;

import com.runtracker.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCrewIdOrderByDateAsc(Long crewId);

    void deleteByMemberId(Long memberId);
}