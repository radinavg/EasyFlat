package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RecordBuilder
public record IngredientDto(
    Long ingredientId,
    @Size(max = 50, message = "One or more ingredients have a name that is longer than 50 characters")
    @NotBlank(message = "One or more ingredients have a name that is blank")
    String name
) {
}
