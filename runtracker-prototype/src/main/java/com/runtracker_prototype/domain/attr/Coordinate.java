package com.runtracker_prototype.domain.attr;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coordinate {
    private Double lat; // 위도
    private Double lnt; // 경도
}
