package com.runtracker_prototype.controller;

import com.runtracker_prototype.dto.CourseDTO;
import com.runtracker_prototype.service.CourseService;
import com.runtracker_prototype.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/proto/courses")
@RequiredArgsConstructor
@Tag(name = "Course API", description = "코스 관련 API")
public class CourseController {

    private final CourseService courseService;
    private final S3Service s3Service;

    @Operation(summary = "반경 내 주변 코스들 검색", description = "현재 위치를 기준으로 반경 내 코스들의 시작점 좌표 리스트를 반환")
    @GetMapping("/nearby")
    public ResponseEntity<List<CourseDTO>> getNearbyCourses() {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    @Operation(summary = "자유 러닝 코스 생성", description = "자유 러닝을 기반으로 코스를 생성")
    @PostMapping("/custom")
    public ResponseEntity<CourseDTO> addCustomCourse() {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new CourseDTO());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CourseDTO());
        }
    }

    @Operation(summary = "코스 상세 보기", description = "코스의 세부 사항을 반환")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new CourseDTO());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CourseDTO());
        }
    }
}
