package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record OpenFoodFactsPackagingsDto(
    @JsonProperty("quantity_per_unit_unit")
    String unit
) {
}
