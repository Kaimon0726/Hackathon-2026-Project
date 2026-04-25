package com.example.demo.service;

import lombok.Data;
import java.util.List;

/**
 * フロントエンドからのレコメンドリクエストを受け取るためのデータ構造
 */
@Data
public class RecommendationRequest {
    private String skinType;      // "Acne", "Dryness", etc.
    private Integer budget;       // 月額予算
    private List<String> allergies; // 除外したい成分
    private String concerns;      // その他悩み事
}
