package com.runtracker_prototype.util;

import com.runtracker_prototype.domain.attr.Coordinate;

public class GeoUtils {
    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (km)

    /**
     * Haversine 공식을 사용하여 두 지점 사이의 거리를 계산합니다.
     * @param point1 첫 번째 좌표
     * @param point2 두 번째 좌표
     * @return 두 지점 사이의 거리 (미터)
     */
    public static double calculateDistance(Coordinate point1, Coordinate point2) {
        double lat1 = Math.toRadians(point1.getLat());
        double lon1 = Math.toRadians(point1.getLnt());
        double lat2 = Math.toRadians(point2.getLat());
        double lon2 = Math.toRadians(point2.getLnt());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 킬로미터를 미터로 변환
        return EARTH_RADIUS_KM * c * 1000;
    }
} 