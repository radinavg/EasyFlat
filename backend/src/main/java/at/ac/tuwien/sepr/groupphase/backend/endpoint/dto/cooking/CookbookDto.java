package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CookbookDto(
    Long id,
    @NotEmpty(message = "The title cannot be empty")
    String title,
    @NotNull(message = "The sharedFlat cannot be null")
    WgDetailDto sharedFlat
) {

}
