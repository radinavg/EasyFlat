package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;

@RecordBuilder
public record ItemListDto(
    @NotEmpty
    String generalName,
    @NotEmpty
    Double quantityCurrent,
    @NotEmpty
    Double quantityTotal,
    @NotEmpty
    Long storageId,
    UnitDto unit


) {
}
