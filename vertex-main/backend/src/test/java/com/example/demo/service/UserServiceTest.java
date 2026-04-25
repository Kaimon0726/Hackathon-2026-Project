package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

//2026.03.17 add test code but not run because of other file...  I have to run after all file is completed.
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }

    private Map<String, Object> createProduct(String name, int price, int interval, String... ingredients) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("price", price);
        userProfile.put("replacementIntervalMonths", interval);
        userProfile.put("ingredients", Arrays.asList(ingredients));
        return userProfile;
    }

    @Nested
    class AllergyTest {
        @Test
        @DisplayName("it is allergy test. If no allergy match, return true")
        void shouldExcludeProductWhenNoAllergyMatches() {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("allergies", "卵,牛乳");

            Map<String, Object> product = createProduct("低刺激ローション", 2000, 1, "水", "グリセリン");

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(product));

            assertThat(result).containsExactly(product);
        }

        @Test
        @DisplayName("if have allergy, it should be exclude product")
        void shouldExcludeProductWhenAllergyMatches() {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("allergies", List.of("エタノール"));

            Map<String, Object> product = createProduct("さっぱり化粧水", 1500, 1, "水", "エタノール");

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(product));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("if user has no allergies, it should not filter any product")
        void shouldNotFilterWhenAllergiesIsNull() {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("allergies", null);

            Map<String, Object> product = createProduct("Product A", 1000, 1, "成分A");

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(product));
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("it is budget test")
    class BudgetTest {
        @Test
        @DisplayName("if monthly cost is within budget, it should keep product")
        void shouldKeepProductWhenWithinBudget() {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("monthlyBudget", 3000);

            Map<String, Object> product = createProduct("美容液", 6000, 2);

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(product));

            assertThat(result).containsExactly(product);
        }

        @Test
        @DisplayName("if monthly cost is over budget, it should exclude product")
        void shouldExcludeProductWhenOverBudget() {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("monthlyBudget", 2000);

            Map<String, Object> product = createProduct("高級クリーム", 3000, 1);

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(product));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("if replacement interval is 0 or negative, it should be calculated as 1 month for budget evaluation")
        void shouldHandleInvalidIntervalAsOneMonth() {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("monthlyBudget", 1000);

            Map<String, Object> product = createProduct("特売品", 1500, 0); // 1500 / 1 = 1500 > 1000

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(product));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("if monthly budget is missing, it should not filter any product based on price")
        void shouldIncludeEverythingWhenBudgetIsMissing() {
            Map<String, Object> userProfile = new HashMap<>();

            Map<String, Object> expensiveProduct = createProduct("超高級品", 100000, 1);

            List<Map<String, Object>> result = userService.getRecommendations(userProfile, List.of(expensiveProduct));

            assertThat(result).containsExactly(expensiveProduct);
        }
    }

}
