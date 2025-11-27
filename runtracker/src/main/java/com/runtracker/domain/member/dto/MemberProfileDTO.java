package com.runtracker.domain.member.dto;

import com.runtracker.domain.member.entity.Member;
import lombok.*;

public class MemberProfileDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
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
        private String createdAt;
        private String updatedAt;

        public static Response from(Member member) {
            return Response.builder()
                    .id(member.getId())
                    .socialAttr(member.getSocialAttr())
                    .socialId(member.getSocialId())
                    .photo(member.getPhoto())
                    .name(member.getName())
                    .introduce(member.getIntroduce())
                    .age(member.getAge())
                    .gender(member.getGender())
                    .region(member.getRegion())
                    .difficulty(member.getDifficulty())
                    .temperature(member.getTemperature())
                    .point(member.getPoint())
                    .searchBlock(member.getSearchBlock())
                    .profileBlock(member.getProfileBlock())
                    .notifyBlock(member.getNotifyBlock())
                    .createdAt(member.getCreatedAt() != null ? member.getCreatedAt().toString() : null)
                    .updatedAt(member.getUpdatedAt() != null ? member.getUpdatedAt().toString() : null)
                    .build();
        }
    }
}