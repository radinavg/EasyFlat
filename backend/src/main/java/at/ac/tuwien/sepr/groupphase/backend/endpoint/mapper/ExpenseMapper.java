package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.RepeatingExpenseType;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;

@Mapper(uses = {UserMapper.class, DebitMapper.class})
public abstract class ExpenseMapper {

    @Mapping(target = "debitUsers", source = "expense")
    @Mapping(target = "isRepeating", expression = "java( expense.getPeriodInDays() != null )")
    @Mapping(target = "periodInDays", expression = "java( convertPeriodInDaysWhenPredefined(expense.getPeriodInDays()) )")
    @Mapping(target = "repeatingExpenseType", expression = "java( convertRepeatingExpenseType(expense.getPeriodInDays()) )")
    public abstract ExpenseDto entityToExpenseDto(Expense expense);

    @Mapping(target = "debitUsers", source = "expense")
    public abstract List<ExpenseDto> entityListToExpenseDtoList(List<Expense> expense);

    @Mapping(target = "splitBy", source = "expenseDto.debitUsers")
    @Mapping(target = "debitUsers", expression = "java( debits )")
    @Mapping(target = "periodInDays", expression = "java( setPeriodOfDaysToCorrectPredefinedRepeatingInterval(expenseDto) )")
    public abstract Expense expenseDtoToExpense(ExpenseDto expenseDto,
                                                List<Debit> debits);

    public SplitBy splitByFromDebitDtoList(List<DebitDto> debitUsersDtoList) {
        return debitUsersDtoList.stream()
            .findAny()
            .orElse(DebitDtoBuilder.builder().splitBy(null).build())
            .splitBy();
    }

    public Integer setPeriodOfDaysToCorrectPredefinedRepeatingInterval(ExpenseDto expenseDto) {
        if (expenseDto.repeatingExpenseType() == null) {
            return expenseDto.periodInDays();
        }
        return expenseDto.repeatingExpenseType().value;
    }

    protected RepeatingExpenseType convertRepeatingExpenseType(Integer periodInDays) {
        if (periodInDays == null || periodInDays > 0) {
            return null;
        }
        return Arrays.stream(RepeatingExpenseType.values())
            .filter(value ->
                value.value == periodInDays
            ).findAny()
            .orElseThrow();
    }

    protected Integer convertPeriodInDaysWhenPredefined(Integer periodInDays) {
        if (periodInDays == null || periodInDays > 0) {
            return periodInDays;
        }
        return null;
    }
}
