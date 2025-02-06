package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookingSteps(
    List<Step> steps
) {
}
