package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;

@RecordBuilder
public record EventLabelDto(
    Long id,
    @NotBlank(message = "Label name cannot be blank")
    String labelName,
    String labelColour
) {

}
