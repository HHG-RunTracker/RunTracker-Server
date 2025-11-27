package com.runtracker.global.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zalando.logbook.*;
import org.zalando.logbook.core.DefaultHttpLogFormatter;

import java.io.IOException;

public class PrettyHttpLogFormatter implements HttpLogFormatter {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final DefaultHttpLogFormatter delegate = new DefaultHttpLogFormatter();

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        return delegate.format(precorrelation, request);
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
        String originalResponse = delegate.format(correlation, response);
        return prettifyJson(originalResponse);
    }

    private String prettifyJson(String logMessage) {
        try {
            if (logMessage.contains("{") && logMessage.contains("}")) {
                int jsonStart = logMessage.indexOf("{");
                int jsonEnd = logMessage.lastIndexOf("}") + 1;

                if (jsonStart != -1 && jsonEnd > jsonStart) {
                    String beforeJson = logMessage.substring(0, jsonStart);
                    String jsonPart = logMessage.substring(jsonStart, jsonEnd);
                    String afterJson = logMessage.substring(jsonEnd);

                    JsonNode jsonNode = MAPPER.readTree(jsonPart);
                    String prettyJson = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

                    return beforeJson + "\n" + prettyJson + afterJson;
                }
            }
            return logMessage;
        } catch (Exception e) {
            return logMessage;
        }
    }
}