package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserProfileDto;
import com.example.demo.service.AiAnalysisService;
import com.example.demo.service.ProductImportService;
import com.example.demo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "${frontend.url}")
public class RecommendationController {
    private final ProductImportService productImportService;
    private final UserService userService;
    private final AiAnalysisService aiAnalysisService;

    public RecommendationController(ProductImportService productImportService, UserService userService,
            AiAnalysisService aiAnalysisService) {
        this.productImportService = productImportService;
        this.userService = userService;
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(@RequestBody UserProfileDto userProfile) {
        System.out.println("Received user profile: " + userProfile);
        try {
            Map<String, Object> userProfileMap = new HashMap<>();
            userProfileMap.put("allergies", userProfile.getAllergies());
            userProfileMap.put("monthlyBudget", userProfile.getMonthlyBudget());
            userProfileMap.put("skinType", userProfile.getSkinType());
            userProfileMap.put("moistureLevel", userProfile.getMoistureLevel());

            String productfilename = "shampoo_db.json";// It need json file at resources/json.
            /*
             * I think the format of json file is like this.
             * [
             * {
             * "name": "A",
             * "ingredients": ["water"],
             * "price": 2000,
             * "replacementIntervalMonths": 1
             * },
             * {
             * "name": "B",
             * "ingredients": ["water"],
             * "price": 2000,
             * "replacementIntervalMonths": 1
             * },
             * {
             * "name": "C",
             * "ingredients": ["water"],
             * "price": 2000,
             * "replacementIntervalMonths": 1
             * }
             * ]
             */

            List<Map<String, Object>> allProducts = productImportService.loadProductsFromJson(productfilename);
            List<Map<String, Object>> recommendations = userService.getRecommendations(userProfileMap, allProducts);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/reason")
    public ResponseEntity<Map<String, String>> getAiReason(@RequestBody Map<String, Object> requestData) {
        try {
            String itemName = (String) requestData.get("itemName");
            String skinType = (String) requestData.get("skinType");
            int moistureLevel = (int) requestData.get("moistureLevel");

            String reason = aiAnalysisService.getRecommendationReason(itemName, skinType, moistureLevel);

            Map<String, String> response = new HashMap<>();
            response.put("reason", reason);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        // AI output send frontend(detail.html)
    }

}