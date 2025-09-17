package com.runtracker.domain.record.repository;

import com.runtracker.domain.record.entity.RunningRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<RunningRecord, Long> {
    
    List<RunningRecord> findByMemberId(Long memberId);
    
    List<RunningRecord> findByMemberIdOrderByRunningTimeDesc(Long memberId);
    
    @Query("SELECT r FROM RunningRecord r WHERE r.memberId = :memberId AND DATE(r.startedAt) BETWEEN :startDate AND :endDate ORDER BY r.startedAt DESC")
    List<RunningRecord> findByMemberIdAndRunningTimeBetween(@Param("memberId") Long memberId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<RunningRecord> findAllByMemberIdAndFinishedAtIsNull(Long memberId);

    List<RunningRecord> findByMemberIdInAndCreatedAtBetweenAndFinishedAtIsNotNull(List<Long> memberIds, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<RunningRecord> findByMemberIdAndCreatedAtBetweenAndFinishedAtIsNotNull(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}