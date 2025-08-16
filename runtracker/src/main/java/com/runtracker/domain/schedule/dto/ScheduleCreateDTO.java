package com.runtracker.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateDTO {

    private Long crewId;
    private String date;
    private String title;
    private String content;
}