package com.runtracker.domain.course.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runtracker.domain.course.dto.NearbyCoursesDTO.Response;
import com.runtracker.domain.course.entity.QCourse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<Response> findNearbyCourses(double lat, double lng, int radiusInMeters) {
        QCourse course = QCourse.course;
        return queryFactory
            .select(Projections.constructor(Response.class,
                course.id, course.memberId, course.name, course.difficulty,
                course.paths, course.startLat, course.startLng, course.distance,
                course.round, course.region,
                Expressions.numberTemplate(Double.class,
                    "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
                    lng, lat, course.startLng, course.startLat
                ).as("distanceFromUser")
            ))
            .from(course)
            .where(Expressions.numberTemplate(Double.class,
                "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
                lng, lat, course.startLng, course.startLat
            ).loe(radiusInMeters))
            .fetch();
    }
}