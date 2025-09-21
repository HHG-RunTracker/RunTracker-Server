package com.runtracker.domain.member.repository;

import com.runtracker.domain.member.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByMemberId(Long memberId);

    @Query("SELECT f.token FROM FcmToken f WHERE f.memberId = :memberId")
    Optional<String> findTokenByMemberId(@Param("memberId") Long memberId);

    void deleteByMemberId(Long memberId);
}