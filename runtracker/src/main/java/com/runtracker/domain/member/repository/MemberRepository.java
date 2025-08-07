package com.runtracker.domain.member.repository;

import com.runtracker.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialId(String socialId);
    boolean existsBySocialId(String socialId);
    Optional<Member> findByName(String name);
}