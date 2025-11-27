package com.runtracker.domain.schedule.dto;

import com.runtracker.domain.schedule.entity.Schedule;
import com.runtracker.global.code.DateConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

public class ScheduleDetailDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long crewId;
        private Long memberId;
        private String date;
        private String title;
        private String content;
        private String creatorName;

        public static Response from(Schedule schedule, String creatorName) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.SHORT_DATETIME_PATTERN);
            
            return Response.builder()
                    .id(schedule.getId())
                    .crewId(schedule.getCrewId())
                    .memberId(schedule.getMemberId())
                    .date(schedule.getDate() != null ? schedule.getDate().format(formatter) : null)
                    .title(schedule.getTitle())
                    .content(schedule.getContent())
                    .creatorName(creatorName)
                    .build();
        }
    }
}