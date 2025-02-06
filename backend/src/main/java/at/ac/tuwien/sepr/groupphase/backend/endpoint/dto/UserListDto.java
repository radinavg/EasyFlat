package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record UserListDto(
    Long id,
    String firstName,
    String lastName
) {

}
