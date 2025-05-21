package com.runtracker_prototype.dto;

import com.runtracker_prototype.domain.Course;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RecordDTO {
    private Long id; // 기록 id
    private Long courseId; // 코스 id
    private LocalDateTime time; // 걸린 시간 (자동 설정)
    private Integer kcal; // 칼로리
    private Integer walkCnt; // 걸음 수

    // 기록 생성 시 사용할 생성자
    public RecordDTO(Long courseId, Integer kcal, Integer walkCnt) {
        this.courseId = courseId;
        this.kcal = kcal;
        this.walkCnt = walkCnt;
    }
}
