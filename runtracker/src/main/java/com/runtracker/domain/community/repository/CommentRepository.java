package com.runtracker.domain.community.repository;

import com.runtracker.domain.community.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {
    void deleteAllByPostId(Long postId);
    long countByPostId(Long postId);
    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}