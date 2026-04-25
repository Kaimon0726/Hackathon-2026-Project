package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AiRequestDto {
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("skin_type")
    private String skinType;
    @JsonProperty("moisture_level")
    private int moistureLevel;

    public AiRequestDto(String itemName, String skinType, int moistureLevel) {
        this.itemName = itemName;
        this.skinType = skinType;
        this.moistureLevel = moistureLevel;
    }
}