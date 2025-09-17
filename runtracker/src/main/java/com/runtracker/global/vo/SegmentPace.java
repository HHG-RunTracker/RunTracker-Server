package com.runtracker.global.vo;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SegmentPace {
    private Double distance;
    private Integer time;
}