package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record OpenFoodFactsCategoryPropertiesDto(
    @JsonProperty("ciqual_food_name:en")
    String description
) {
}
