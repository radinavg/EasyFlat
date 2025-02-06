package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record OpenFoodFactsIngredientDto(
    String text
) {
}
