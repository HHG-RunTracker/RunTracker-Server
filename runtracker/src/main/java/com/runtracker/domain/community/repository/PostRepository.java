package com.runtracker.domain.community.repository;

import com.runtracker.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByCrewIdOrderByCreatedAtDesc(Long crewId);
    
    List<Post> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    
    @Query("SELECT p FROM Post p WHERE p.crewId = :crewId ORDER BY p.createdAt DESC")
    List<Post> findPostsByCrewId(@Param("crewId") Long crewId);
}