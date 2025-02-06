package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record ItemSearchDto(
    @NotNull
    Boolean alwaysInStock,
    String productName,
    String fillLevel,
    ItemOrderType orderType,
    Boolean desc
) {
}
