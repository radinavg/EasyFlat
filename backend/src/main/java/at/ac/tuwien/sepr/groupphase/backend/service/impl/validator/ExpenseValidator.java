package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ExpenseValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Validator validator;

    public ExpenseValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateExpenseForSearch(ExpenseSearchDto expenseSearchDto) throws ValidationException {
        LOGGER.trace("validateExpenseForSearch({})", expenseSearchDto);

        Set<ConstraintViolation<ExpenseSearchDto>> validationViolations = validator.validate(expenseSearchDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The search data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    public void validateExpenseForCreate(ExpenseDto expenseDto,
                                         List<ApplicationUser> applicationUsersOfFlat) throws ValidationException, ConflictException {
        LOGGER.trace("validateExpenseForCreate({}, {})", expenseDto, applicationUsersOfFlat);

        validateExpenseDto(expenseDto);
        checkForConflictForCreate(expenseDto, applicationUsersOfFlat);
    }

    public void validateExpenseForUpdate(ExpenseDto expenseDto,
                                         List<ApplicationUser> applicationUsersOfFlat) throws ValidationException, ConflictException {
        LOGGER.trace("validateExpenseForUpdate({}, {})", expenseDto, applicationUsersOfFlat);

        validateExpenseDto(expenseDto);
        checkForConflictForUpdate(expenseDto, applicationUsersOfFlat);

    }

    private void validateExpenseDto(ExpenseDto expenseDto) throws ValidationException {
        LOGGER.trace("validateExpenseDto({})", expenseDto);

        Set<ConstraintViolation<ExpenseDto>> validationViolations = validator.validate(expenseDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkForConflictForCreate(ExpenseDto expenseDto,
                                           List<ApplicationUser> applicationUsers) throws ConflictException {
        LOGGER.trace("checkForConflictForCreate({}, {})", expenseDto, applicationUsers);

        List<String> errors = new ArrayList<>();

        if (expenseDto.id() != null) {
            errors.add("The Id must be null");
        }

        if (expenseDto.paidBy() == null) {
            errors.add("The payer cannot not be empty");
        } else if (applicationUsers.stream()
            .noneMatch(user ->
                user.getId().equals(expenseDto.paidBy().id())
            )) {
            errors.add("The payer must be a member of the flat");
        }

        if (expenseDto.debitUsers() == null) {
            errors.add("The list of users responsible for the payment cannot be empty.");
        } else if (expenseDto.debitUsers().isEmpty()) {
            errors.add("The list of users responsible for the payment cannot be empty.");
        } else if (expenseDto.debitUsers().stream()
            .anyMatch(user ->
                applicationUsers.stream()
                    .noneMatch(applicationUser ->
                        applicationUser.getId().equals(user.user().id())
                    )
            )) {
            errors.add("The list of users responsible for the payment can only contain flat members.");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("Conflict with persisted data", errors);
        }

    }

    private void checkForConflictForUpdate(ExpenseDto expenseDto,
                                           List<ApplicationUser> applicationUsers) throws ConflictException {
        LOGGER.trace("checkForConflictForUpdate({}, {})", expenseDto, applicationUsers);

        List<String> errors = new ArrayList<>();

        if (expenseDto.id() == null) {
            errors.add("The expense id cannot be empty");
        }

        if (expenseDto.paidBy() == null) {
            errors.add("The payer cannot not be empty");
        } else if (applicationUsers.stream()
            .noneMatch(user ->
                user.getId().equals(expenseDto.paidBy().id())
            )) {
            errors.add("The payer must be a member of the flat");
        }

        if (expenseDto.debitUsers() == null) {
            errors.add("The list of users responsible for the payment cannot be empty.");
        } else if (expenseDto.debitUsers().isEmpty()) {
            errors.add("The list of users responsible for the payment cannot be empty.");
        } else if (expenseDto.debitUsers().stream()
            .anyMatch(user ->
                applicationUsers.stream()
                    .noneMatch(applicationUser ->
                        applicationUser.getId().equals(user.user().id())
                    )
            )) {
            errors.add("The list of users responsible for the payment can only contain flat members.");
        }

        if (!errors.isEmpty()) {
            throw new ConflictException("Conflict with persisted data", errors);
        }

    }
}
