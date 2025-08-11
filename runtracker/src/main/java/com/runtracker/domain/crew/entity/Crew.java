package com.runtracker.domain.crew.entity;

import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "crew")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "introduce", columnDefinition = "TEXT")
    private String introduce;

    @Column(name = "region", length = 100)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    private Difficulty difficulty;

    @Column(name = "crews", columnDefinition = "JSON")
    private String crews;

    @Column(name = "schedules", columnDefinition = "JSON")
    private String schedules;

    @Column(name = "leader_id", nullable = false)
    private Long leaderId;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updatePhoto(String photo) {
        this.photo = photo;
    }

    public void updateIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void updateRegion(String region) {
        this.region = region;
    }

    public void updateDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}