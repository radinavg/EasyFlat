package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record OpenFoodFactsResponseDto(
    @JsonProperty("code")
    String eanCode,
    OpenFoodFactsProductDto product,
    boolean status
) {
}
