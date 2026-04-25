package com.example.demo.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String allergies;
    private Integer monthlyBudget;
    private String skinType;
    private Integer moistureLevel;
}
// it is dto for user profile,
// it will be used to receive user input from frontend and pass it to service
// layer for processing.

// and..I have to say to Ryo that develop form this format. about allergies,
// monthlyBuget 2026.03.18
