package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.UserProfileDto;
import com.example.demo.service.AiAnalysisService;
import com.example.demo.service.ProductImportService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
public class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductImportService productImportService;
    @MockBean
    private UserService userService;
    @MockBean
    private AiAnalysisService aiAnalysisService;

    @Test
    public void testGetRecommendations() throws Exception {
        UserProfileDto requestDto = new UserProfileDto();
        requestDto.setAllergies("water");
        requestDto.setMonthlyBudget(1000);

        List<Map<String, Object>> mockResponse = List.of(
                Map.of("name", "A", "ingredients", List.of("water"), "price", 2000, "replacementIntervalMonths", 1),
                Map.of("name", "B", "ingredients", List.of("oil"), "price", 500, "replacementIntervalMonths", 1));

        when(productImportService.loadProductsFromJson(anyString())).thenReturn(List.of());
        when(userService.getRecommendations(anyMap(), anyList())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[0].ingredients").isArray())
                .andExpect(jsonPath("$[0].price").value(2000))
                .andExpect(jsonPath("$[1].name").value("B"))
                .andExpect(jsonPath("$[1].ingredients").isArray())
                .andExpect(jsonPath("$[1].price").value(500));
    }

    @Test
    public void testGetAiReason() throws Exception {
        Map<String, Object> requestDataMap = Map.of(
                "itemName", "Shampoo A",
                "skinType", "乾燥肌",
                "moistureLevel", 40);

        String outputReason = "最適な湿度だから";

        when(aiAnalysisService.getRecommendationReason(anyString(), anyString(), anyInt()))
                .thenReturn(outputReason);
        mockMvc.perform(post("/api/recommendations/reason")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDataMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value(outputReason));
    }
}
