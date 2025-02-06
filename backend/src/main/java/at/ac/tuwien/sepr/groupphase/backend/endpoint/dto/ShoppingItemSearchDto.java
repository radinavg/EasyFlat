package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record ShoppingItemSearchDto(
    Long itemId,
    String productName,
    String label
) {
}
