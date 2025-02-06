package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RecordBuilder
public record ChoreSearchDto(
    String userName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {
}
