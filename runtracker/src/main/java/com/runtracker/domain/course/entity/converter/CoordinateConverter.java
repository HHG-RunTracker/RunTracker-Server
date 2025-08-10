package com.runtracker.domain.course.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runtracker.domain.course.entity.vo.Coordinate;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class CoordinateConverter implements AttributeConverter<Coordinate, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(coordinate);
        } catch (JsonProcessingException e) {
            log.error("JSON writing error", e);
            return null;
        }
    }

    @Override
    public Coordinate convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Coordinate.class);
        } catch (JsonProcessingException e) {
            log.error("JSON reading error", e);
            return null;
        }
    }
}