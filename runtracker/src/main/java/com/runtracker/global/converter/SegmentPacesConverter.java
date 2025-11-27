package com.runtracker.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runtracker.global.vo.SegmentPace;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Converter
@Slf4j
public class SegmentPacesConverter implements AttributeConverter<List<SegmentPace>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<SegmentPace> segmentPaces) {
        if (segmentPaces == null || segmentPaces.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(segmentPaces);
        } catch (JsonProcessingException e) {
            log.error("JSON writing error", e);
            return null;
        }
    }

    @Override
    public List<SegmentPace> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<SegmentPace>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON reading error", e);
            return new ArrayList<>();
        }
    }
}