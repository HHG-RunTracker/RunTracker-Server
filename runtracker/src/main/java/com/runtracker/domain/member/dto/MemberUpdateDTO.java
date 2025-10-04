package com.runtracker.domain.member.dto;

import lombok.*;

public class MemberUpdateDTO {
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String photo;
        private String name;
        private String introduce;
        private String region;
        private String difficulty;
        private Integer age;
        private Boolean gender;
        private Boolean searchBlock;
        private Boolean profileBlock;
    }
}