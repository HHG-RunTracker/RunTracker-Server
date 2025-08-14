package com.runtracker.domain.crew.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.crew.entity.Crew;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CrewListDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String photo;
        private String introduce;
        private String region;
        private Difficulty difficulty;
        private Long leaderId;
        private Integer memberCount;
        private LocalDateTime createdAt;

        public static Response from(Crew crew, Integer memberCount) {
            return Response.builder()
                    .id(crew.getId())
                    .title(crew.getTitle())
                    .photo(crew.getPhoto())
                    .introduce(crew.getIntroduce())
                    .region(crew.getRegion())
                    .difficulty(crew.getDifficulty())
                    .leaderId(crew.getLeaderId())
                    .memberCount(memberCount)
                    .createdAt(crew.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private List<Response> crews;
        private Integer totalCount;

        public static ListResponse of(List<Response> crews) {
            return ListResponse.builder()
                    .crews(crews)
                    .totalCount(crews.size())
                    .build();
        }
    }
}