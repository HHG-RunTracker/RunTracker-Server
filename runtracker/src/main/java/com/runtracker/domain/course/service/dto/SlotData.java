package com.runtracker.domain.course.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotData implements Serializable {
    private String location;  // 그룹 키
    @Builder.Default
    private List<Long> courseIds = new ArrayList<>();
    private LocalDateTime lastAccessTime;
    private Integer frequency;

    public void updateAccess() {
        this.lastAccessTime = LocalDateTime.now();
        this.frequency = (this.frequency == null ? 0 : this.frequency) + 1;
    }

    public void addCourseId(Long courseId) {
        if (this.courseIds == null) {
            this.courseIds = new ArrayList<>();
        }
        if (!this.courseIds.contains(courseId)) {
            this.courseIds.add(courseId);
        }
    }
}