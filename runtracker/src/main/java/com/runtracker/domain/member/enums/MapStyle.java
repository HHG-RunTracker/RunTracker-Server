package com.runtracker.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MapStyle {
    STANDARD("기본 지도"),
    SATELLITE("위성 지도"),
    HYBRID("하이브리드 지도");

    private final String description;
}