package com.runtracker.global.client;

import com.runtracker.global.code.CommonResponseCode;
import com.runtracker.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class FastAPIClient {

    @Value("${app.domain}")
    private String appDomain;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getBaseUrl() {
        return appDomain + ":8000";
    }

    public <R> R get(String endpoint, Class<R> responseType) {
        try {
            String url = getBaseUrl() + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to call FastAPI GET {}: {}", endpoint, e.getMessage(), e);
            throw new CustomException(CommonResponseCode.EXTERNAL_API_ERROR, "FastAPI request failed: " + endpoint);
        }
    }

    public <T, R> R post(String endpoint, T requestBody, Class<R> responseType) {
        try {
            String url = getBaseUrl() + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<R> response = restTemplate.postForEntity(url, entity, responseType);

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to call FastAPI POST {}: {}", endpoint, e.getMessage(), e);
            throw new CustomException(CommonResponseCode.EXTERNAL_API_ERROR, "FastAPI request failed: " + endpoint);
        }
    }
}