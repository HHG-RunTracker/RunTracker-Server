package com.runtracker.domain.community.entity;

import com.runtracker.global.converter.StringListConverter;
import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "photos", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> photos;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "time")
    private Integer time;

    @Column(name = "avg_pace")
    private Double avgPace;

    @Column(name = "avg_speed")
    private Double avgSpeed;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePhotos(List<String> photos) {
        this.photos = photos;
    }

    public void updateRunningMeta(Double distance, Integer time, Double avgPace, Double avgSpeed) {
        this.distance = distance;
        this.time = time;
        this.avgPace = avgPace;
        this.avgSpeed = avgSpeed;
    }
}