package com.runtracker.domain.crew.dto;

import com.runtracker.domain.course.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CrewUpdateDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String photo;
        private String introduce;
        private String region;
        private Difficulty difficulty;
    }
}