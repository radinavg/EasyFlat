package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.UserValuePairDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/expense")
public class ExpenseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    public ExpenseEndpoint(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("{id}")
    public ExpenseDto findById(@PathVariable("id") Long id) throws AuthorizationException {
        LOGGER.info("findById: {}", id);

        return expenseMapper.entityToExpenseDto(
            expenseService.findById(id)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping()
    public List<ExpenseDto> findAll(ExpenseSearchDto expenseSearchDto) throws ValidationException {
        LOGGER.info("findAll()");
        return expenseMapper.entityListToExpenseDtoList(
            expenseService.findAll(expenseSearchDto)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("debits")
    public List<BalanceDebitDto> calculateDebits() throws AuthorizationException {
        LOGGER.info("calculateDebits");

        return expenseService.calculateDebits();
    }

    @Secured("ROLE_USER")
    @GetMapping("statistics/expenses")
    public List<UserValuePairDto> calculateTotalExpensesPerUser() throws AuthorizationException {
        LOGGER.info("calculateExpenses()");

        return expenseService.calculateTotalExpensesPerUser();
    }

    @Secured("ROLE_USER")
    @GetMapping("statistics/debits")
    public List<UserValuePairDto> calculateTotalDebitsPerUser() throws AuthorizationException {
        LOGGER.info("calculateDebits()");

        return expenseService.calculateTotalDebitsPerUser();
    }

    @Secured("ROLE_USER")
    @GetMapping("statistics/balance")
    public List<UserValuePairDto> calculateBalancePerUser() throws AuthorizationException {
        LOGGER.info("calculateBalance()");

        return expenseService.calculateBalancePerUser();
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseDto create(@RequestBody ExpenseDto expenseDto) throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.info("create: {}", expenseDto);

        return expenseMapper.entityToExpenseDto(
            expenseService.create(expenseDto)
        );
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    public ExpenseDto update(@PathVariable long id, @RequestBody ExpenseDto expenseDto) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.info("update({},{})", id, expenseDto);
        return expenseMapper.entityToExpenseDto(
            expenseService.update(expenseDto.withId(id))
        );
    }

    @Secured("ROLE_USER")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws AuthorizationException {
        LOGGER.info("delete({})", id);
        expenseService.delete(id);
    }

}
