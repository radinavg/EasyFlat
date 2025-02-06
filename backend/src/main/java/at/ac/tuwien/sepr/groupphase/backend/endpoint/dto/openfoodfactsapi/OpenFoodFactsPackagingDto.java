package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
public record OpenFoodFactsPackagingDto(
    List<OpenFoodFactsPackagingsDto> packagings
) {
}
