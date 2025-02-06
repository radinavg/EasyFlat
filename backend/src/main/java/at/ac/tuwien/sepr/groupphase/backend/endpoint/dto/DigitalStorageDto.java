package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record DigitalStorageDto(
    Long storageId,
    @NotEmpty(message = "The title cannot be empty") String title,
    @NotNull(message = "The sharedFlat cannot be null") WgDetailDto sharedFlat
) {
}
