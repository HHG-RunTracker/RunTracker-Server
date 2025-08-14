package com.runtracker.domain.crew.dto;

import com.runtracker.domain.member.entity.Member;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileDTO {
    private Long memberId;
    private String socialId;
    private String photo;
    private String name;
    private String introduce;
    private Integer age;
    private Boolean gender;
    private String region;
    private String difficulty;
    private Double temperature;

    public static MemberProfileDTO from(Member member) {
        return MemberProfileDTO.builder()
                .memberId(member.getId())
                .socialId(member.getSocialId())
                .photo(member.getPhoto())
                .name(member.getName())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .region(member.getRegion())
                .difficulty(member.getDifficulty())
                .temperature(member.getTemperature())
                .build();
    }
}