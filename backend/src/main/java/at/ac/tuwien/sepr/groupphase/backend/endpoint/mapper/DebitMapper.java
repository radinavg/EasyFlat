package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UserMapper.class})
public abstract class DebitMapper {

    @Mapping(target = "user", source = "debit.id.user")
    @Mapping(target = "value", expression = "java( convertPercentToValue(debit.getPercent(), totalValue, splitBy) )")
    @Mapping(target = "splitBy", expression = "java( splitBy )")
    public abstract DebitDto entityToDebitDto(Debit debit,
                                              SplitBy splitBy,
                                              double totalValue);


    @Mapping(target = "id.user", source = "debitDto.user")
    @Mapping(target = "id.expense", source = "expenseDto")
    @Mapping(target = "percent", source = "debitDto.value")
    public abstract Debit debitDtoToEntity(DebitDto debitDto,
                                           ExpenseDto expenseDto);

    public List<Debit> debitDtoListToEntityList(ExpenseDto expenseDto) {
        return expenseDto.debitUsers().stream()
            .map(debitDto ->
                debitDtoToEntity(debitDto, expenseDto)
            ).toList();
    }

    public List<DebitDto> entityListToDebitDtoList(Expense expense) {
        return expense.getDebitUsers().stream()
            .map(debit ->
                entityToDebitDto(debit, expense.getSplitBy(), expense.getAmountInCents())
            ).toList();
    }

    /**
     * Converts a percent value to a value.
     *
     * @param percent    percent value
     * @param totalValue total value
     * @param splitBy    split by strategy
     * @return value according to the split by strategy
     */
    protected double convertPercentToValue(double percent,
                                           double totalValue,
                                           SplitBy splitBy) {
        return switch (splitBy) {
            case EQUAL, UNEQUAL -> percent * totalValue / 100.0;
            case PERCENTAGE, PROPORTIONAL -> percent;
        };
    }
}
