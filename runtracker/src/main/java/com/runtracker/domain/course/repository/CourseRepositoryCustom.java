package com.runtracker.domain.course.repository;

import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;

import java.util.List;

public interface CourseRepositoryCustom {
    List<Response> findNearbyCourses(double lat, double lng);
}