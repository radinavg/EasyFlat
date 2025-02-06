package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.UserValuePairDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DebitMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final DebitMapper debitMapper;
    private final Authorization authorization;
    private final AuthService authService;
    private final UserMapper userMapper;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseMapper expenseMapper,
                              ExpenseValidator expenseValidator,
                              DebitMapper debitMapper,
                              Authorization authorization,
                              AuthService authService,
                              UserMapper userMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.expenseValidator = expenseValidator;
        this.debitMapper = debitMapper;
        this.authorization = authorization;
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @Override
    public Expense findById(Long id) throws NotFoundException, AuthorizationException {
        LOGGER.trace("findById({})", id);

        Expense persistedExpense = expenseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Expense not found"));

        List<ApplicationUser> allowedUsers = persistedExpense.getPaidBy().getSharedFlat().getUsers().stream().toList();

        authorization.authorizeUser(
            allowedUsers.stream().map(ApplicationUser::getId).toList(),
            "User does not have access to this expense"
        );

        return persistedExpense;
    }

    @Override
    public List<Expense> findAll(ExpenseSearchDto expenseSearchDto) throws ValidationException {
        LOGGER.trace("findAll({})", expenseSearchDto);

        expenseValidator.validateExpenseForSearch(expenseSearchDto);

        Long flatId = authService.getUserFromToken().getSharedFlat().getId();

        LocalDateTime fromCreatedAt = null;
        LocalDateTime toCreatedAt = null;

        if (expenseSearchDto.fromCreatedAt() != null) {
            fromCreatedAt = expenseSearchDto.fromCreatedAt().atStartOfDay();
        }
        if (expenseSearchDto.toCreatedAt() != null) {
            toCreatedAt = expenseSearchDto.toCreatedAt().atTime(23, 59, 59);
        }

        return expenseRepository.findByCriteria(
            flatId,
            expenseSearchDto.title(),
            expenseSearchDto.paidById(),
            expenseSearchDto.minAmountInCents(),
            expenseSearchDto.maxAmountInCents(),
            fromCreatedAt,
            toCreatedAt
        );
    }

    @Override
    public List<Expense> findRepeatingExpenses() {
        LOGGER.trace("findRepeatingExpenses()");

        return expenseRepository.findAllByPeriodInDaysIsNotNull();
    }

    @Override
    public List<UserValuePairDto> calculateTotalExpensesPerUser() {
        LOGGER.trace("calculateTotalExpensesPerUser()");

        Set<ApplicationUser> usersOfSharedFlat = authService.getUserFromToken().getSharedFlat().getUsers();
        Map<ApplicationUser, Double> totalAmountPaidPerUser = calculateTotalAmountPaidPerUserOfSharedFlat(usersOfSharedFlat);

        return totalAmountPaidPerUser.entrySet().stream().map(
            entry -> new UserValuePairDto(
                userMapper.entityToUserListDto(entry.getKey()),
                (Math.abs(entry.getValue()) < 1) ? 0 : entry.getValue()
            )
        ).collect(Collectors.toList());
    }

    @Override
    public List<UserValuePairDto> calculateTotalDebitsPerUser() {
        LOGGER.trace("calculateTotalDebitsPerUser()");

        Set<ApplicationUser> usersOfSharedFlat = authService.getUserFromToken().getSharedFlat().getUsers();
        Map<ApplicationUser, Double> totalAmountOwedPerUser = calculateTotalAmountOwedPerUserOfSharedFlat(usersOfSharedFlat);

        return totalAmountOwedPerUser.entrySet().stream().map(
            entry -> new UserValuePairDto(
                userMapper.entityToUserListDto(entry.getKey()),
                (Math.abs(entry.getValue()) < 1) ? 0 : entry.getValue()
            )
        ).collect(Collectors.toList());
    }

    @Override
    public List<UserValuePairDto> calculateBalancePerUser() {
        LOGGER.trace("calculateBalancePerUser()");

        Set<ApplicationUser> usersOfSharedFlat = authService.getUserFromToken().getSharedFlat().getUsers();
        Map<ApplicationUser, Double> balancesPerUser = this.calculateDifferenceBetweenPaidAndOwedAmountPerUser(
            calculateTotalAmountPaidPerUserOfSharedFlat(usersOfSharedFlat),
            calculateTotalAmountOwedPerUserOfSharedFlat(usersOfSharedFlat)
        ).stream().collect(
            Collectors.toMap(
                Pair::getUser,
                Pair::getAmount
            )
        );

        return balancesPerUser.entrySet().stream().map(
            entry -> new UserValuePairDto(
                userMapper.entityToUserListDto(entry.getKey()),
                (Math.abs(entry.getValue()) < 1) ? 0 : entry.getValue()
            )
        ).collect(Collectors.toList());
    }

    @Override
    public List<BalanceDebitDto> calculateDebits() {
        LOGGER.trace("calculateDebits()");

        Set<ApplicationUser> usersOfSharedFlat = authService.getUserFromToken().getSharedFlat().getUsers();

        Map<ApplicationUser, Double> totalAmountPaidPerUser = calculateTotalAmountPaidPerUserOfSharedFlat(usersOfSharedFlat);

        Map<ApplicationUser, Double> totalAmountOwedPerUser = calculateTotalAmountOwedPerUserOfSharedFlat(usersOfSharedFlat);

        List<Pair> differenceOrdered = calculateDifferenceBetweenPaidAndOwedAmountPerUser(totalAmountPaidPerUser, totalAmountOwedPerUser);

        List<BalanceDebitDto> balanceDebitDtos = new ArrayList<>();

        while (differenceOrdered.size() > 1) {
            Pair debtor = differenceOrdered.get(0);
            Pair creditor = differenceOrdered.get(differenceOrdered.size() - 1);

            double toPay = Math.min(-debtor.getAmount(), creditor.getAmount());

            creditor.amount -= toPay;
            debtor.amount += toPay;

            if (toPay >= 1) {
                addDebitToList(debtor, creditor, toPay, balanceDebitDtos);
            }

            if (debtor.getAmount() == 0) {
                differenceOrdered.remove(debtor);
            }
            if (creditor.getAmount() == 0) {
                differenceOrdered.remove(creditor);
            }

            differenceOrdered.sort(Pair::compareTo);
        }

        return balanceDebitDtos;
    }

    @Override
    @Transactional
    public Expense create(ExpenseDto expenseDto) throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.trace("create: {}", expenseDto);

        ApplicationUser user = authService.getUserFromToken();
        List<ApplicationUser> usersOfFlat = user.getSharedFlat().getUsers().stream().toList();

        expenseValidator.validateExpenseForCreate(expenseDto, usersOfFlat);

        authorization.authorizeUser(
            usersOfFlat.stream().map(ApplicationUser::getId).toList(),
            "You cannot create an expense for this flat"
        );

        List<Debit> debitList = defineDebitPerUserBySplitBy(
            expenseDto,
            expenseDto.debitUsers().stream().findAny().orElseThrow().splitBy()
        );

        Expense expense = expenseMapper.expenseDtoToExpense(expenseDto, debitList);

        expense.getDebitUsers().forEach(debit -> debit.getId().setExpense(expense));
        return expenseRepository.save(expense);
    }

    @Override
    @Transactional
    public Expense update(ExpenseDto expenseDto) throws ConflictException, ValidationException, AuthorizationException {
        LOGGER.trace("update: {}", expenseDto);

        findById(expenseDto.id());

        ApplicationUser user = authService.getUserFromToken();
        List<ApplicationUser> usersOfFlat = user.getSharedFlat().getUsers().stream().toList();

        expenseValidator.validateExpenseForUpdate(expenseDto, usersOfFlat);

        authorization.authorizeUser(
            usersOfFlat.stream().map(ApplicationUser::getId).toList(),
            "You are not allowed to update an expense for this flat"
        );

        List<Debit> debitList = defineDebitPerUserBySplitBy(
            expenseDto,
            expenseDto.debitUsers().stream().findAny().orElseThrow().splitBy()
        );

        Expense expense = expenseMapper.expenseDtoToExpense(expenseDto, debitList);

        return expenseRepository.save(expense);
    }

    @Override
    @Transactional
    public void delete(Long id) throws AuthorizationException {
        LOGGER.trace("delete({})", id);

        findById(id);

        expenseRepository.deleteById(id);
    }

    private List<Debit> defineDebitPerUserBySplitBy(ExpenseDto expense, SplitBy splitBy) throws ValidationException {
        LOGGER.trace("defineDebitPerUserBySplitBy({})", expense);

        if (expense.amountInCents() < expense.debitUsers().size() && splitBy == SplitBy.EQUAL) {
            return amountIsNotSplittable(expense, expense.amountInCents());
        }

        List<Debit> debitList = new ArrayList<>();
        switch (splitBy) {
            case EQUAL, UNEQUAL -> expense.debitUsers().forEach(debitDto -> {
                DebitDto debit = DebitDtoBuilder.builder()
                    .splitBy(debitDto.splitBy())
                    .user(debitDto.user())
                    .value(debitDto.value() * 100.0 / expense.amountInCents())
                    .build();
                debitList.add(debitMapper.debitDtoToEntity(debit, expense));
            });
            case PERCENTAGE ->
                expense.debitUsers().forEach(debitDto -> debitList.add(debitMapper.debitDtoToEntity(debitDto, expense)));
            case PROPORTIONAL -> {
                double proportions = expense.debitUsers().stream().mapToDouble(DebitDto::value).sum();
                double baseAmount = expense.amountInCents() / proportions;
                expense.debitUsers().forEach(debitDto -> {
                    double valueOfUser = baseAmount * debitDto.value();
                    DebitDto debit = DebitDtoBuilder.builder()
                        .splitBy(debitDto.splitBy())
                        .user(debitDto.user())
                        .value(valueOfUser * 100.0 / expense.amountInCents())
                        .build();
                    debitList.add(debitMapper.debitDtoToEntity(debit, expense));
                });
            }
            default ->
                throw new ValidationException("Unexpected value: " + splitBy, List.of("Unexpected value: " + splitBy));
        }

        adaptValueIfPaidAmountIsNotTotalAmount(debitList, expense.amountInCents());

        return debitList;
    }

    private List<Debit> amountIsNotSplittable(ExpenseDto expenseDto, double totalAmount) {
        LOGGER.trace("adaptIfPaidAmountIsNotTotalAmount({},{})", expenseDto, totalAmount);

        List<Double> amountPerUser = new ArrayList<>();
        expenseDto.debitUsers().forEach(debit -> amountPerUser.add(0.0));

        int idx = 0;
        double remaining = totalAmount;
        while (remaining > 0) {
            amountPerUser.set(idx, amountPerUser.get(idx) + 1);
            idx = ++idx % expenseDto.debitUsers().size();
            remaining--;
        }

        List<DebitDto> debitDtoList = expenseDto.debitUsers();
        List<Debit> debitListResult = new ArrayList<>();
        for (int i = 0; i < expenseDto.debitUsers().size(); i++) {
            DebitDto toSaveDebit = DebitDtoBuilder.builder()
                .splitBy(debitDtoList.get(i).splitBy())
                .user(debitDtoList.get(i).user())
                .value(amountPerUser.get(i) / totalAmount * 100.0)
                .build();
            debitListResult.add(debitMapper.debitDtoToEntity(toSaveDebit, expenseDto));
        }

        return debitListResult;
    }

    private void adaptValueIfPaidAmountIsNotTotalAmount(List<Debit> debitList, double totalAmount) {
        LOGGER.trace("adaptIfPaidAmountIsNotTotalAmount({},{})", debitList, totalAmount);

        double difference = Math.round(differenceBetweenTotalAmountAndPaidPerUser(debitList, totalAmount));
        double oneCentInPercent = 1 / totalAmount * 100.0;
        double d = difference;
        for (int i = 0; d > 0; i++, d--) {
            debitList.get(i).setPercent(debitList.get(i).getPercent() + oneCentInPercent);
        }
        d = difference;
        for (int i = 0; d < 0; i++, d++) {
            debitList.get(i).setPercent(debitList.get(i).getPercent() - oneCentInPercent);
        }
    }

    private double differenceBetweenTotalAmountAndPaidPerUser(List<Debit> debitList, double totalAmount) {
        LOGGER.trace("differenceBetweenTotalAmountAndPaidPerUser()");

        return totalAmount - debitList.stream()
            .mapToDouble(debit ->
                Math.round(debit.getPercent() / 100.0 * totalAmount)
            ).sum();
    }

    private void addDebitToList(Pair debtor, Pair creditor, double toPay, List<BalanceDebitDto> balanceDebitDtos) {
        LOGGER.trace("addDebitToList({},{},{},{})", debtor, creditor, toPay, balanceDebitDtos);
        BalanceDebitDto balanceDebitDto = BalanceDebitDtoBuilder.builder()
            .debtor(userMapper.entityToUserListDto(debtor.getUser()))
            .creditor(userMapper.entityToUserListDto(creditor.getUser()))
            .valueInCent(toPay)
            .build();

        balanceDebitDtos.add(balanceDebitDto);
    }

    private List<Pair> calculateDifferenceBetweenPaidAndOwedAmountPerUser(Map<ApplicationUser, Double> totalAmountPaidPerUser,
                                                                          Map<ApplicationUser, Double> totalAmountOwedPerUser) {
        LOGGER.trace("calculateDifferenceBetweenPaidAndOwedAmountPerUser({},{})", totalAmountPaidPerUser, totalAmountOwedPerUser);
        return totalAmountPaidPerUser.entrySet().stream().map(
                entry -> new Pair(
                    entry.getKey(),
                    entry.getValue() - totalAmountOwedPerUser.getOrDefault(entry.getKey(), 0.0)
                )
            ).sorted()
            .collect(Collectors.toList());
    }

    private Map<ApplicationUser, Double> calculateTotalAmountOwedPerUserOfSharedFlat(Set<ApplicationUser> usersOfSharedFlat) {
        LOGGER.trace("calculateTotalAmountOwedPerUserOfSharedFlat({})", usersOfSharedFlat);
        return expenseRepository.findByPaidByIsIn(
            usersOfSharedFlat
        ).stream().flatMap(
            expense -> expense.getDebitUsers().stream()
        ).collect(
            Collectors.groupingBy(
                debit -> debit.getId().getUser(),
                Collectors.summingDouble(debit ->
                    debit.getId()
                        .getExpense()
                        .getAmountInCents() * debit.getPercent() / 100.0
                )
            )
        );
    }

    private Map<ApplicationUser, Double> calculateTotalAmountPaidPerUserOfSharedFlat(Set<ApplicationUser> usersOfSharedFlat) {
        LOGGER.trace("calculateTotalAmountPaidPerUserOfSharedFlat({})", usersOfSharedFlat);
        Map<ApplicationUser, Double> totalAmountPaidPerUserFound = expenseRepository.findByPaidByIsIn(
            usersOfSharedFlat
        ).stream().collect(
            Collectors.groupingBy(
                Expense::getPaidBy,
                Collectors.summingDouble(Expense::getAmountInCents)
            )
        );

        Map<ApplicationUser, Double> totalAmountPaidPerUser = new HashMap<>();
        for (ApplicationUser user : usersOfSharedFlat) {
            totalAmountPaidPerUser.put(user, totalAmountPaidPerUserFound.getOrDefault(user, 0.0));
        }
        return totalAmountPaidPerUser;
    }

    private class Pair implements Comparable<Pair> {
        private ApplicationUser user;
        private Double amount;

        public Pair(ApplicationUser user, Double amount) {
            this.user = user;
            this.amount = amount;
        }

        public ApplicationUser getUser() {
            return user;
        }

        public Double getAmount() {
            return amount;
        }


        @Override
        public int compareTo(Pair o) {
            return this.amount.compareTo(o.amount);
        }
    }
}
