package com.runtracker.domain.course.entity.vo;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Coordinate {
    private Double lat;
    private Double lnt;
}