package com.runtracker.global.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Coordinate {
    private Double lat;

    @JsonAlias({"lng"})
    private Double lnt;
}