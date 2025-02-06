package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@RecordBuilder
public record ChoreDto(
    Long id,

    @NotBlank(message = "The name cannot be empty")
    @Size(max = 40, message = "The name is too long")
    String name,

    @Size(max = 100, message = "The description cannot have more than 100 characters")
    String description,

    @NotNull(message = "The deadline must be specified")
    @FutureOrPresent(message = "The deadline must be in the present or in the future")
    LocalDate endDate,

    String points,

    UserDetailDto user
) {
    public ChoreDto trimmedName(String name) {
        return new ChoreDto(this.id, name, this.description, this.endDate, this.points, this.user);
    }

    public ChoreDto trimmed(String name, String description) {
        return new ChoreDto(this.id, name, description, this.endDate, this.points, this.user);
    }
}
