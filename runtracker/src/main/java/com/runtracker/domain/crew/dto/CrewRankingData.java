package com.runtracker.domain.crew.dto;

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

    public void addRecord(double distance, int runningTime) {
        this.totalDistance += distance;
        this.totalRunningTime += runningTime;
        this.participantCount++;
    }
}