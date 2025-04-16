package com.runtracker_prototype.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Record {
    @Id
    @GeneratedValue
    @Column(name = "record_id")
    private Long id;

    private LocalDateTime time; // 걸린 시간

    private Integer kcal; // 칼로리
    
    private Integer walkCnt; // 걸음 수
    
    /* 연관 관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
}
