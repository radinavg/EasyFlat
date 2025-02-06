package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record AlternativeNameDto(
    Long id,
    String name,
    Long shareFlatId
) {

}
