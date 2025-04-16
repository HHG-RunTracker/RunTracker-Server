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
    private LocalDateTime time; // 걸린 시간
    private Integer kcal; // 칼로리
    private Integer walkCnt; // 걸음 수
}
