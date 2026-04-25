package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * フロントエンドから送られてくる肌質定義
 */
@Getter
@AllArgsConstructor
public enum SkinType {
    ACNE("Acne", "ニキビ、肌荒れ"),
    DRYNESS("Dryness", "乾燥、つっぱり"),
    OILINESS("Oiliness", "皮脂、てかり"),
    REDNESS("Redness", "赤み、敏感肌"),
    UNKNOWN("Unknown", "未設定/不明");

    private final String value;
    private final String description;

    /**
     * 文字列からSkinTypeを取得します。
     * 無効な入力やnullの場合はUNKNOWNを返します。
     */
    public static SkinType fromString(String text) {
        if (text == null || text.isBlank()) {
            return UNKNOWN;
        }
        for (SkinType b : SkinType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
