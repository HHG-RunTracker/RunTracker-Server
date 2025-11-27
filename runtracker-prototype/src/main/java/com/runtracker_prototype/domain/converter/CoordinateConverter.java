package com.runtracker_prototype.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runtracker_prototype.domain.attr.Coordinate;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

/**
 * JSON <-> Coordinate Converter
 */

@Converter
public class CoordinateConverter implements AttributeConverter<Coordinate, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Coordinate attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Coordinate to JSON", e);
        }
    }

    @Override
    public Coordinate convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, Coordinate.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading Coordinate from JSON", e);
        }
    }
}
