package com.runtracker.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BackupType {
    ORIGINAL("저장된 백업"),
    RESTORED("복원된 백업");

    private final String description;
}