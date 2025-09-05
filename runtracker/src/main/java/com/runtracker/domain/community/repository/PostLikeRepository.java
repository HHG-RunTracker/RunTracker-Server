package com.runtracker.domain.community.repository;

import com.runtracker.domain.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLike.PostLikeId> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    
    void deleteByPostIdAndMemberId(Long postId, Long memberId);
    
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.postId = :postId")
    long countLikesByPostId(@Param("postId") Long postId);
}