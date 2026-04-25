package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AiResponseDto {
    @JsonProperty("item_Name")
    private String itemName;
    @JsonProperty("skin_type")
    private String skinType;
    @JsonProperty("moisture_level")
    private int moistureLevel;
    @JsonProperty("recommend_reason")
    private String recommendReason;
}
