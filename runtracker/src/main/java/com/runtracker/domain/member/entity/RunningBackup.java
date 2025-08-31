package com.runtracker.domain.member.entity;

import com.runtracker.domain.member.enums.BackupType;
import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "running_backup")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RunningBackup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "backup_data", columnDefinition = "JSON")
    private String backupData;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "backup_type", nullable = false)
    private BackupType backupType;
    
    public void updateBackupData(String backupData) {
        this.backupData = backupData;
        this.backupType = BackupType.ORIGINAL;
    }
    
    public void markAsRestored() {
        this.backupType = BackupType.RESTORED;
    }
}