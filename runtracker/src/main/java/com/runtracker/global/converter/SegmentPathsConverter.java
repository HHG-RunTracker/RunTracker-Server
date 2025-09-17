package com.runtracker.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runtracker.global.vo.Coordinate;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Converter
@Slf4j
public class SegmentPathsConverter implements AttributeConverter<List<List<Coordinate>>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<List<Coordinate>> segmentPaths) {
        if (segmentPaths == null || segmentPaths.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(segmentPaths);
        } catch (JsonProcessingException e) {
            log.error("JSON writing error", e);
            return null;
        }
    }

    @Override
    public List<List<Coordinate>> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<List<Coordinate>>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON reading error", e);
            return new ArrayList<>();
        }
    }
}