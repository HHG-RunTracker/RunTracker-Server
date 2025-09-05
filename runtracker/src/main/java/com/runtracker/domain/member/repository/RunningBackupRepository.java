package com.runtracker.domain.member.repository;

import com.runtracker.domain.member.entity.RunningBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RunningBackupRepository extends JpaRepository<RunningBackup, Long> {
    Optional<RunningBackup> findByMemberId(Long memberId);
}