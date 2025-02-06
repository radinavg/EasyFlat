package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record BalanceDebitDto(
    UserListDto debtor,
    UserListDto creditor,
    Double valueInCent
) {
}

