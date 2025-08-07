package com.runtracker.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

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

    @Column(name = "gender")
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Member(String socialAttr, String socialId, String photo, String name, 
                  String introduce, Integer age, Boolean gender, String region, 
                  String difficulty, Double temperature, Integer point, 
                  Boolean searchBlock, Boolean profileBlock, Boolean notifyBlock) {
        this.socialAttr = socialAttr;
        this.socialId = socialId;
        this.photo = photo;
        this.name = name;
        this.introduce = introduce;
        this.age = age;
        this.gender = gender;
        this.region = region;
        this.difficulty = difficulty;
        this.temperature = temperature != null ? temperature : 36.5;
        this.point = point != null ? point : 0;
        this.searchBlock = searchBlock != null ? searchBlock : false;
        this.profileBlock = profileBlock != null ? profileBlock : false;
        this.notifyBlock = notifyBlock != null ? notifyBlock : true;
    }

    public void updateProfile(String name, String introduce, Integer age, Boolean gender, 
                            String region, String difficulty) {
        this.name = name;
        this.introduce = introduce;
        this.age = age;
        this.gender = gender;
        this.region = region;
        this.difficulty = difficulty;
    }

    public void updatePhoto(String photo) {
        this.photo = photo;
    }
}