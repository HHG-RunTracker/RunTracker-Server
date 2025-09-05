package com.runtracker.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class RunningBackupDTO {
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberBackupData {
        private Long id;
        private String name;
        private String socialId;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecordBackupData {
        private Long id;
        private Long memberId;
        private Long courseId;
        private Long crewRunningId;
        private Integer runningTime;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
        private Double distance;
        private Integer walk;
        private Integer calorie;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BackupData {
        private MemberBackupData member;
        private List<RecordBackupData> runningRecords;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BackupInfo {
        private Long backupId;
        private String backupType;
        private Integer recordCount;
        private String updatedAt;
    }
}