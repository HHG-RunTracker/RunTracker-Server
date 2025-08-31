package com.runtracker.domain.crew.dto;

import lombok.Getter;

@Getter
public class MemberRankingData {
    private final Long memberId;
    private double totalDistance = 0.0;
    private int totalRunningTime = 0;
    private int participationCount = 0;

    public MemberRankingData(Long memberId) {
        this.memberId = memberId;
    }

    public void addRecord(double distance, int runningTime) {
        this.totalDistance += distance;
        this.totalRunningTime += runningTime;
        this.participationCount++;
    }
}