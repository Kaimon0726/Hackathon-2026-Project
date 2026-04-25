package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.AiRequestDto;
import com.example.demo.dto.AiResponseDto;

@Service
public class AiAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);
    private final RestTemplate restTemplate;
    @Value("${ML_SERVICE_URL}")
    private String mlServiceUrl;

    public AiAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getRecommendationReason(String itemName, String skinType, int moistureLevel) {
        String pythonApiUrl = mlServiceUrl + "/recommend";
        AiRequestDto request = new AiRequestDto(itemName, skinType, moistureLevel);

        try {
            log.info("Sending API request to: {}", pythonApiUrl);
            AiResponseDto response = restTemplate.postForObject(pythonApiUrl, request, AiResponseDto.class);

            if (response == null || response.getRecommendReason() == null) {
                log.warn("Received null response from AI service for item: {}", itemName);
                return "mistake catch AI data";
            }
            return response.getRecommendReason();

        } catch (Exception e) {
            log.error("cause of AiAnalysisService.java", e);
            return "mistake catch AI data";
        }
    }
}
