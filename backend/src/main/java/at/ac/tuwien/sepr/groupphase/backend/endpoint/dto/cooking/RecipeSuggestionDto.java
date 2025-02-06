package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSuggestionDto(
    Long id,
    @NotBlank(message = "The title cannot be blank")
    @Size(max = 200, message = "The title cannot exceed 200 characters.")
    String title,
    @NotNull(message = "The servings cannot be empty")
    @Min(value = 1, message = "The servings must be positive")
    @Max(value = 1000, message = "The servings cannot be more than 1000")
    Integer servings,
    @NotNull(message = "The time in minutes cannot be empty")
    @Min(value = 1, message = "The time in minutes must be positive")
    @Max(value = 1000, message = "The time in minutes cannot be greater than 1000 min")
    Integer readyInMinutes,
    @Valid
    List<RecipeIngredientDto> extendedIngredients,
    @NotBlank(message = "The summary cannot be empty")
    @Size(max = 3000, message = "The title cannot exceed 3000 characters.")
    String summary,
    List<RecipeIngredientDto> missedIngredients,
    List<String> dishTypes) {

    public RecipeSuggestionDto withId(Long newId) {
        return new RecipeSuggestionDto(newId, title, servings, readyInMinutes, extendedIngredients, summary, missedIngredients, dishTypes);
    }

    public RecipeSuggestionDto withExtendedIngredients(List<RecipeIngredientDto> newRecipeIngredientDtos) {
        return new RecipeSuggestionDto(id, title, servings, readyInMinutes, newRecipeIngredientDtos, summary, missedIngredients, dishTypes);
    }

    public RecipeSuggestionDto withSummaryAndWithoutMissingIngredients(String newSummary) {
        return new RecipeSuggestionDto(id, title, servings, readyInMinutes, extendedIngredients, newSummary, new ArrayList<>(), dishTypes);
    }


}
