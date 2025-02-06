package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

@RecordBuilder
public record UnitDto(
    String name,
    @Nullable Long convertFactor,
    @Nullable Set<UnitDto> subUnit
) {

    public UnitDto {
        if (subUnit == null) {
            subUnit = new HashSet<>();
        }
    }
}
