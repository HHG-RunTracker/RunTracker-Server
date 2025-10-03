package com.runtracker.domain.member.entity;

import com.runtracker.global.entity.BaseEntity;
import com.runtracker.domain.member.dto.MemberCreateDTO;
import com.runtracker.domain.member.dto.MemberUpdateDTO;
import com.runtracker.domain.member.dto.RunningSettingDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_attr", length = 20)
    private String socialAttr;

    @Column(name = "social_id", unique = true, nullable = false)
    private String socialId;

    @Column(name = "photo")
    private String photo;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "introduce", columnDefinition = "TEXT")
    private String introduce;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", columnDefinition = "TINYINT(1)")
    private Boolean gender;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "temperature", columnDefinition = "DOUBLE DEFAULT 36.5")
    private Double temperature = 36.5;

    @Column(name = "point", columnDefinition = "INT DEFAULT 0")
    private Integer point = 0;

    @Column(name = "search_block", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean searchBlock = false;

    @Column(name = "profile_block", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean profileBlock = false;

    @Column(name = "notify_block", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean notifyBlock = true;

    @Column(name = "radius", columnDefinition = "INT DEFAULT 500")
    private Integer radius = 500;

    @Column(name = "daily_distance_goal", columnDefinition = "DOUBLE")
    private Double dailyDistanceGoal;

    @Column(name = "monthly_run_count_goal", columnDefinition = "INT")
    private Integer monthlyRunCountGoal;

    @Column(name = "preferred_difficulty", length = 20)
    private String preferredDifficulty;

    @Column(name = "auto_pause", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean autoPause = true;

    @Column(name = "map_style", length = 50)
    private String mapStyle;

    @Column(name = "pace_unit", columnDefinition = "INT")
    private Integer paceUnit;

    @Column(name = "tts_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ttsEnabled = true;

    @Builder
    public Member(MemberCreateDTO dto) {
        this.socialAttr = dto.getSocialAttr();
        this.socialId = dto.getSocialId();
        this.photo = dto.getPhoto();
        this.name = dto.getName();
        this.introduce = dto.getIntroduce();
        this.age = dto.getAge();
        this.gender = dto.getGender();
        this.region = dto.getRegion();
        this.difficulty = dto.getDifficulty();
        this.temperature = dto.getTemperature() != null ? dto.getTemperature() : 36.5;
        this.point = dto.getPoint() != null ? dto.getPoint() : 0;
        this.searchBlock = dto.getSearchBlock() != null ? dto.getSearchBlock() : false;
        this.profileBlock = dto.getProfileBlock() != null ? dto.getProfileBlock() : false;
        this.notifyBlock = dto.getNotifyBlock() != null ? dto.getNotifyBlock() : true;
        this.radius = dto.getRadius() != null ? dto.getRadius() : 500;
    }

    public void updateProfile(MemberUpdateDTO.Request dto) {
        if (dto.getPhoto() != null) this.photo = dto.getPhoto();
        if (dto.getName() != null) this.name = dto.getName();
        if (dto.getIntroduce() != null) this.introduce = dto.getIntroduce();
        if (dto.getAge() != null) this.age = dto.getAge();
        if (dto.getGender() != null) this.gender = dto.getGender();
        if (dto.getRegion() != null) this.region = dto.getRegion();
        if (dto.getDifficulty() != null) this.difficulty = dto.getDifficulty();
        if (dto.getSearchBlock() != null) this.searchBlock = dto.getSearchBlock();
        if (dto.getProfileBlock() != null) this.profileBlock = dto.getProfileBlock();
    }

    public void updatePhoto(String photo) {
        this.photo = photo;
    }
    
    public void updateTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public void updateNotificationSetting(Boolean notifyBlock) {
        if (notifyBlock != null) {
            this.notifyBlock = notifyBlock;
        }
    }

    public void updateRunningSetting(RunningSettingDTO.Request runningSettingDTO) {
        if (runningSettingDTO.getDailyDistanceGoal() != null) this.dailyDistanceGoal = runningSettingDTO.getDailyDistanceGoal();
        if (runningSettingDTO.getMonthlyRunCountGoal() != null) this.monthlyRunCountGoal = runningSettingDTO.getMonthlyRunCountGoal();
        if (runningSettingDTO.getPreferredDifficulty() != null) this.preferredDifficulty = runningSettingDTO.getPreferredDifficulty();
        if (runningSettingDTO.getAutoPause() != null) this.autoPause = runningSettingDTO.getAutoPause();
        if (runningSettingDTO.getMapStyle() != null) this.mapStyle = runningSettingDTO.getMapStyle();
        if (runningSettingDTO.getRadius() != null) this.radius = runningSettingDTO.getRadius();
        if (runningSettingDTO.getPaceUnit() != null) this.paceUnit = runningSettingDTO.getPaceUnit();
        if (runningSettingDTO.getTtsEnabled() != null) this.ttsEnabled = runningSettingDTO.getTtsEnabled();
    }
}