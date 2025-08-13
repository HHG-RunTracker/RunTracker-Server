package com.runtracker.domain.schedule.entity;

import com.runtracker.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "members", columnDefinition = "JSON")
    private String members;

    public void updateSchedule(LocalDateTime date, String title, String content) {
        if (date != null) {
            this.date = date;
        }
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }

    public void joinSchedule(Long memberId) {
        List<Long> memberList = getParticipants();
        if (!memberList.contains(memberId)) {
            memberList.add(memberId);
            updateMembersJson(memberList);
        }
    }

    public void cancelSchedule(Long memberId) {
        List<Long> memberList = getParticipants();
        memberList.remove(memberId);
        updateMembersJson(memberList);
    }

    public List<Long> getParticipants() {
        if (members == null || members.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(members, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void updateMembersJson(List<Long> memberList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.members = objectMapper.writeValueAsString(memberList);
        } catch (Exception e) {
            this.members = "[]";
        }
    }
}