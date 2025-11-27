package com.runtracker.domain.schedule.dto;

import com.runtracker.domain.schedule.entity.Schedule;
import com.runtracker.global.code.DateConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleListDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String date;
        private String title;
        private String content;
        private String creatorName;

        public static Response from(Schedule schedule, String creatorName) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.SHORT_DATETIME_PATTERN);
            
            return Response.builder()
                    .id(schedule.getId())
                    .date(schedule.getDate() != null ? schedule.getDate().format(formatter) : null)
                    .title(schedule.getTitle())
                    .content(schedule.getContent())
                    .creatorName(creatorName)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private List<Response> schedules;

        public static ListResponse of(List<Response> schedules) {
            return ListResponse.builder()
                    .schedules(schedules)
                    .build();
        }
    }
}