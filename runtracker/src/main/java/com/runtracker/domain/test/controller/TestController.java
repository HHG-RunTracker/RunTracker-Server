package com.runtracker.domain.test.controller;

import com.runtracker.global.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// TODO: REST Docs 테스트를 위해 임시로 생성된 파일. 추후 삭제해야함
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public ApiResponse<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "RunTracker API Test Endpoint");
        response.put("status", "success");
        response.put("description", "This is a test endpoint for REST Docs validation");
        return ApiResponse.ok(response);
    }
}