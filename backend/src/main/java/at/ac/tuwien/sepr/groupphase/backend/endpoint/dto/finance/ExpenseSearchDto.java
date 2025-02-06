package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RecordBuilder
public record ExpenseSearchDto(
    @Size(max = 150, message = "You cannot search for a title with more than 150 characters")
    String title,
    Long paidById,
    @Min(value = 0, message = "The minimum amount must be at least 0")
    @Max(value = 1_000_000, message = "The minimum amount can be at most 10.000 €")
    Double minAmountInCents,
    @Min(value = 0, message = "The maximum amount must be at least 0")
    @Max(value = 1_000_000, message = "The maximum amount can be at most 10.000 €")
    Double maxAmountInCents,
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate fromCreatedAt,
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate toCreatedAt
) {
    @AssertTrue(message = "The minimum amount cannot be larger than the maximum amount")
    private boolean isMinimumAmountLessThanOrEqualToMaximumAmount() {
        if (this.minAmountInCents == null) {
            return true;
        }
        if (this.maxAmountInCents == null) {
            return true;
        }
        return this.minAmountInCents <= this.maxAmountInCents;
    }

    @AssertTrue(message = "The from date needs to be before (or the same as) the to date")
    private boolean isFromCreatedAtBeforeToCreatedAt() {
        if (this.fromCreatedAt == null) {
            return true;
        }
        if (this.toCreatedAt == null) {
            return true;
        }
        return this.fromCreatedAt.isBefore(toCreatedAt) || this.fromCreatedAt.isEqual(toCreatedAt);
    }
}
