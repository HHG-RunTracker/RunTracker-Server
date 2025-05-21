package com.runtracker_prototype.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearbyCourses {
    private Double latitude;  // 현재 위치 위도
    private Double longitude; // 현재 위치 경도
    private Integer radiusInMeters; // 검색 반경 (미터)
} 