package com.runtracker.domain.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateDTO {
    private String socialAttr;
    private String socialId;
    private String photo;
    private String name;
    private String introduce;
    private Integer age;
    private Boolean gender;
    private String region;
    private String difficulty;
    private Double temperature;
    private Integer point;
    private Boolean searchBlock;
    private Boolean profileBlock;
    private Boolean notifyBlock;
    private Integer radius;
}