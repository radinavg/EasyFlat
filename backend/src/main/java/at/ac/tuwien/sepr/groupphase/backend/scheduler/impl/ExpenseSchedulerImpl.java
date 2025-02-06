package at.ac.tuwien.sepr.groupphase.backend.scheduler.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.scheduler.ExpenseScheduler;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpenseSchedulerImpl implements ExpenseScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    public ExpenseSchedulerImpl(ExpenseService expenseService,
                                ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void createRepeatingExpense() {
        LOGGER.info("createRepeatingExpense() called");

        List<Expense> repeatingExpenses = expenseService.findRepeatingExpenses();

        repeatingExpenses.stream().filter(this::shouldCreateExpenseToday).forEach(expense -> {
            LOGGER.info("Creating expense for {}", expense.getTitle());
            try {
                expenseService.create(convertToNewExpense(expense));
            } catch (ValidationException e) {
                LOGGER.error("Could not create repeating expense for {}, because of a ValidationException", expense, e);
            } catch (ConflictException e) {
                LOGGER.error("Could not create repeating expense for {}, because of a ConflictException", expense, e);
            } catch (AuthorizationException e) {
                LOGGER.error("Could not create repeating expense for {}, because of a AuthorizationException", expense, e);
            }
        });
    }

    private boolean shouldCreateExpenseToday(Expense expense) {
        return (expense.getPeriodInDays() < 0 && shouldCreatePredefinedExpensesToday(expense))
            || expense.getCreatedAt().plusDays(expense.getPeriodInDays()).toLocalDate().isEqual(LocalDate.now());
    }

    private boolean shouldCreatePredefinedExpensesToday(Expense expense) {
        LocalDateTime now = LocalDateTime.now();
        return switch (expense.getPeriodInDays()) {
            case -1 -> now.getDayOfMonth() == 1;
            case -2 -> now.getDayOfMonth() == 1 && now.getMonthValue() % 3 == 0;
            case -3 -> now.getDayOfYear() == 1;
            default -> {
                LOGGER.error("There is an inconsistency with repeating expenses. "
                    + "The periodInDays was {}, but that should not be possible", expense.getPeriodInDays());
                yield false;
            }
        };
    }

    private ExpenseDto convertToNewExpense(Expense expense) {
        expense.setCreatedAt(LocalDateTime.now());
        expense.setId(null);
        return expenseMapper.entityToExpenseDto(expense);
    }
}
