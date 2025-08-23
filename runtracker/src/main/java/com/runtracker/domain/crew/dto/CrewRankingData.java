package com.runtracker.domain.crew.dto;

import com.runtracker.domain.crew.entity.CrewRecord;
import lombok.Getter;

@Getter
public class CrewRankingData {
    private final Long crewId;
    private double totalDistance = 0.0;
    private int totalRunningTime = 0;
    private int participantCount = 0;

    public CrewRankingData(Long crewId) {
        this.crewId = crewId;
    }

    public void addRecord(CrewRecord record) {
        this.totalDistance += record.getDistance();
        this.totalRunningTime += record.getRunningTime();
        this.participantCount++;
    }
}