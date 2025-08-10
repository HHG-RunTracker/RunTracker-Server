package com.runtracker.domain.record.repository;

import com.runtracker.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Course, Long> {
    
    @Query("SELECT c FROM Course c WHERE c.memberId = :memberId AND DATE(c.createdAt) BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<Course> findByMemberIdAndCreatedAtBetween(@Param("memberId") Long memberId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
}
