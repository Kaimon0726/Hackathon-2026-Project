package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class UserService {

    /**
     * ユーザー属性に基づいて製品をフィルタリングし、推奨リストを返します。
     * @param userProfile ユーザー情報（肌質、アレルギー、予算など）
     * @param allProducts 全製品データ
     * @return フィルタリング後の製品リスト
     */
    public List<Map<String, Object>> getRecommendations(Map<String, Object> userProfile, List<Map<String, Object>> allProducts) {
        // nullチェックを行い、未設定の場合はフィルタリングを無効化（INFまたは空文字）する
        Object allergiesObj = userProfile.get("allergies");
        List<String> allergies = null;
        if (allergiesObj instanceof String) {
            allergies = Arrays.asList(((String) allergiesObj).split(","));
        } else if (allergiesObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) allergiesObj;
            allergies = list;
        }

        Object budgetObj = userProfile.get("monthlyBudget");
        Integer monthlyBudget = (budgetObj instanceof Integer) ? (Integer) budgetObj : Integer.MAX_VALUE;

        return getRecommendationsInternal(allergies, monthlyBudget, allProducts);
    }

    /**
     * RecommendationRequest形式でのリクエストに対応します。
     */
    public List<Map<String, Object>> getRecommendations(RecommendationRequest request, List<Map<String, Object>> allProducts) {
        if (request == null) return allProducts;
        return getRecommendationsInternal(request.getAllergies(), request.getBudget(), allProducts);
    }

    private List<Map<String, Object>> getRecommendationsInternal(List<String> allergies, Integer monthlyBudget, List<Map<String, Object>> allProducts) {
        int budget = (monthlyBudget != null) ? monthlyBudget : Integer.MAX_VALUE;
        
        return allProducts.stream()
                .filter(product -> !hasAllergy(product, allergies))
                .filter(product -> isWithinBudget(product, budget))
                .collect(Collectors.toList());
    }

    /**
     * 製品にアレルギー成分が含まれているか判定します。
     */
    private boolean hasAllergy(Map<String, Object> product, List<String> allergies) {
        if (allergies == null || allergies.isEmpty()) return false;
        
        @SuppressWarnings("unchecked")
        List<String> ingredients = (List<String>) product.get("ingredients");
        if (ingredients == null) return false;

        for (String allergy : allergies) {
            if (allergy == null) continue;
            String target = allergy.trim();
            if (target.isEmpty()) continue;
            if (ingredients.contains(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 製品の月額コストが、ユーザーの予算内か判定します。
     */
    private boolean isWithinBudget(Map<String, Object> product, Integer monthlyBudget) {
        Integer price = (Integer) product.get("price");
        Integer interval = (Integer) product.getOrDefault("replacementIntervalMonths", 1);
        
        if (price == null) return true;
        
        int monthlyCost = price / (interval > 0 ? interval : 1);
        return monthlyCost <= monthlyBudget;
    }
}