package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.UserValuePairDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service for working with Expenses.
 */
public interface ExpenseService {

    /**
     * Finds an expense by id.
     *
     * @param id the id of the expense
     * @return the expense if found
     * @throws NotFoundException if the expense is not persisted
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    Expense findById(Long id) throws NotFoundException, AuthorizationException;

    /**
     * Finds all expenses in the database for the given search parameters or all expenses if search is null.
     *
     * @return any expense that was found for the given search parameters, empty if there are none
     */
    List<Expense> findAll(ExpenseSearchDto expenseSearchDto) throws ValidationException;

    /**
     * Finds all repeating expenses in the database.
     *
     * @return a List of repeating expenses
     */
    List<Expense> findRepeatingExpenses();

    /**
     * Calculates the debits for the current users shared flat.
     *
     * @return a List of BalanceDebitDtos which represent the debits
     */
    List<BalanceDebitDto> calculateDebits();

    /**
     * Calculates the total expenses per user.
     *
     * @return a List of UserValuePairDtos which represent the total expenses per user. The User is the key
     */
    List<UserValuePairDto> calculateTotalExpensesPerUser();

    /**
     * Calculates the total debits per user.
     *
     * @return a List of UserValuePairDtos which represent the total debits per user. The User is the key
     */
    List<UserValuePairDto> calculateTotalDebitsPerUser();

    /**
     * Calculates the balance per user.
     *
     * @return a List of UserValuePairDtos which represent the balance per user. The User is the key
     */
    List<UserValuePairDto> calculateBalancePerUser();

    /**
     * Creates a new expense.
     *
     * @param expenseDto the expense to create
     * @return the created expense with id
     * @throws ValidationException if the expense is not valid
     * @throws ConflictException   if the expense would produce an inconsistent state in the database
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    Expense create(ExpenseDto expenseDto) throws ValidationException, ConflictException, AuthorizationException;

    /**
     * Validates and Updates an {@link Expense} in the db.
     *
     * @param expenseDto an expense with existing ID
     * @return an object of type {@link Expense} which is updated
     * @throws ValidationException if the expense is not valid
     * @throws ConflictException   if the expense would produce an inconsistent state in the database
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    Expense update(ExpenseDto expenseDto) throws ConflictException, ValidationException, AuthorizationException;

    /**
     * Removes an {@link Expense} stored in the db.
     *
     * @param id an ID of a stored {@link Expense}
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    void delete(Long id) throws AuthorizationException;
}
