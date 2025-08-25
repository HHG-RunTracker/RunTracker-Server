package com.runtracker.domain.community.repository;

import com.runtracker.domain.community.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {
    void deleteAllByPostId(Long postId);
}