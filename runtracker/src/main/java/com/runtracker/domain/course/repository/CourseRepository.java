package com.runtracker.domain.course.repository;

import com.runtracker.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
    
    /**
     * 특정 회원의 모든 코스 삭제
     */
    void deleteByMemberId(Long memberId);
}