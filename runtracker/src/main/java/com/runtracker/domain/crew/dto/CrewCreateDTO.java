package com.runtracker.domain.crew.dto;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.domain.crew.entity.Crew;
import lombok.*;

public class CrewCreateDTO {

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
        
        public Crew toEntity(Long leaderId) {
            return Crew.builder()
                    .title(this.title)
                    .photo(this.photo)
                    .introduce(this.introduce)
                    .region(this.region)
                    .difficulty(this.difficulty)
                    .leaderId(leaderId)
                    .build();
        }
    }

}