package com.runtracker.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempCalcService {
    
    private static final double BASE_TEMPERATURE_INCREASE = 0.1; // 기본 온도 증가량
    private static final double COMPLETION_MULTIPLIER = 1.0; // 완주 시 배수
    private static final double INCOMPLETE_MULTIPLIER = 0.7; // 미완주 시 배수
    private static final double COMPLETION_THRESHOLD = 0.9; // 완주 판정 기준 (90%)

    // 완주 여부 판정
    public boolean isCompleted(double completionRate) {
        return completionRate >= COMPLETION_THRESHOLD;
    }

    /**
     * 러닝 기록에 따른 온도 계산
     * 
     * @param currentTemperature 현재 온도
     * @param actualDistance 실제 러닝한 거리
     * @param courseDistance 코스의 총 거리
     * @return 새로운 온도 (최대 100도)
     */
    public double calculateNewTemperature(double currentTemperature, double actualDistance, double courseDistance) {
        if (courseDistance <= 0) {
            return currentTemperature;
        }
        
        double completionRate = actualDistance / courseDistance;
        double multiplier = isCompleted(completionRate) ? COMPLETION_MULTIPLIER : INCOMPLETE_MULTIPLIER;
        double distanceFactor = Math.min(actualDistance / 1000.0, 10.0);
        
        double temperatureIncrease = BASE_TEMPERATURE_INCREASE * multiplier * (1 + distanceFactor * 0.1);
        double newTemperature = currentTemperature + temperatureIncrease;
        
        return Math.min(newTemperature, 100.0);
    }
}