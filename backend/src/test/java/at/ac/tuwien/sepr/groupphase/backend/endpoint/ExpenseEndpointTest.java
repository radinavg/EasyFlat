package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.BalanceDebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.ExpenseDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance.UserValuePairDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DebitMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.unauthorizedExpenseId;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validExpenseId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ExpenseEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private DebitMapper debitMapper;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    private final String BASE_URI = "/api/v1/expense";
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Endpoint test for findById with matching ID")
    void findById() throws Exception {
        // given
        Expense expected = expenseRepository.findById(validExpenseId).orElseThrow();
        List<DebitDto> expectedDebitDtoList = debitMapper.entityListToDebitDtoList(expected);

        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/" + validExpenseId)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        ExpenseDto expenseDto = objectMapper.readValue(response.getContentAsString(), ExpenseDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(expenseDto).extracting(
                ExpenseDto::id,
                ExpenseDto::title,
                ExpenseDto::description,
                ExpenseDto::amountInCents,
                ExpenseDto::createdAt,
                ExpenseDto::isRepeating,
                expenseDto1 -> expenseDto1.paidBy().id()
            ).contains(
                validExpenseId,
                expected.getTitle(),
                expected.getDescription(),
                expected.getAmountInCents(),
                expected.getCreatedAt(),
                expected.getPeriodInDays() != null && expected.getPeriodInDays() < 0,
                expected.getPaidBy().getId()
            ),
            () -> assertThat(expenseDto.debitUsers()).isEqualTo(expectedDebitDtoList)
        );
    }

    @Test
    @DisplayName("Endpoint test for findById of not allowed ID")
    void findByIdNotAuthorized() throws Exception {
        // given
        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/" + unauthorizedExpenseId)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    @DisplayName("Test if findAll returns all expenses that belong to the current users flat")
    void findAll() throws Exception {
        // given
        Long currentFlatId = applicationUser.getSharedFlat().getId();

        List<Expense> expectedExpenseList = expenseRepository.findAll().stream()
            .filter(expense -> expense.getPaidBy().getSharedFlat().getId().equals(currentFlatId))
            .toList();

        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ExpenseDto.class);
        List<ExpenseDto> actualExpenseDtoList = objectMapper.readValue(response.getContentAsString(), type);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(actualExpenseDtoList).hasSameSizeAs(expectedExpenseList),
            () -> assertThat(actualExpenseDtoList).containsExactlyInAnyOrderElementsOf(
                expectedExpenseList.stream().map(expenseMapper::entityToExpenseDto).collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("Test if calculate debits creates a correct response")
    void calculateDebits() throws Exception {
        // given
        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/debits")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        TypeReference<List<BalanceDebitDto>> typeReference = new TypeReference<>() {
        };
        List<BalanceDebitDto> actual = objectMapper.readValue(response.getContentAsString(), typeReference);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(actual).extracting(
                (BalanceDebitDto balanceDebitDto) -> balanceDebitDto.debtor().id(),
                (BalanceDebitDto balanceDebitDto) -> balanceDebitDto.creditor().id(),
                (BalanceDebitDto balanceDebitDto) -> Math.round(balanceDebitDto.valueInCent() * 10) / 10.0
            ).contains(
                new Tuple(1L, 6L, 4577.9),
                new Tuple(1L, 11L, 988.2),
                new Tuple(21L, 11L, 459.6),
                new Tuple(21L, 16L, 1451.9)
            )
        );
    }

    @Test
    @DisplayName("Test if calculate total expenses per user creates a correct response")
    void calculateTotalExpensesPerUser() throws Exception {
        // given
        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/statistics/expenses")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        TypeReference<List<UserValuePairDto>> typeReference = new TypeReference<>() {
        };
        List<UserValuePairDto> actual = objectMapper.readValue(response.getContentAsString(), typeReference);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(actual).size().isEqualTo(applicationUser.getSharedFlat().getUsers().size()),
            () -> assertThat(actual.stream()
                .map(pair ->
                    pair.user().id()
                ).toList()
            ).containsAnyElementsOf(
                applicationUser.getSharedFlat()
                    .getUsers()
                    .stream()
                    .map(ApplicationUser::getId)
                    .toList()
            ),
            () -> assertThat(actual.stream()
                .map(pair ->
                    Math.round((pair.value()) * 10) / 10.0
                ).toList()
            ).containsAll(
                List.of(
                    1726.6,
                    3188.4,
                    4440.7,
                    5372.9,
                    6764.2
                )
            )
        );
    }

    @Test
    @DisplayName("Test if calculate total debit per user creates a correct response")
    void calculateTotalDebitsPerUser() throws Exception {
        // given
        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/statistics/debits")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        TypeReference<List<UserValuePairDto>> typeReference = new TypeReference<>() {
        };
        List<UserValuePairDto> actual = objectMapper.readValue(response.getContentAsString(), typeReference);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(actual).size().isEqualTo(applicationUser.getSharedFlat().getUsers().size()),
            () -> assertThat(actual.stream()
                .map(pair ->
                    pair.user().id()
                ).toList()
            ).containsAnyElementsOf(
                applicationUser.getSharedFlat()
                    .getUsers()
                    .stream()
                    .map(ApplicationUser::getId)
                    .toList()
            ),
            () -> assertThat(actual.stream()
                .map(pair ->
                    Math.floor(Math.round((pair.value()) * 10) / 10.0)
                ).toList()
            ).containsAll(
                List.of(
                    274.0,
                    1740.0,
                    2186.0,
                    7284.0,
                    10006.0
                )
            )
        );
    }

    @Test
    @DisplayName("Test if calculate balances per user creates a correct response")
    void calculateBalancePerUser() throws Exception {
        // given
        // when
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/statistics/balance")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        TypeReference<List<UserValuePairDto>> typeReference = new TypeReference<>() {
        };
        List<UserValuePairDto> actual = objectMapper.readValue(response.getContentAsString(), typeReference);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).extracting(
                (UserValuePairDto userValuePairDto) -> userValuePairDto.user().id(),
                (UserValuePairDto userValuePairDto) -> Math.round((userValuePairDto.value()) * 10) / 10.0
            ).containsExactlyInAnyOrder(
                new Tuple(1L, -5566.1),
                new Tuple(6L, 4577.9),
                new Tuple(11L, 1447.7),
                new Tuple(16L, 1451.9),
                new Tuple(21L, -1911.5)
            )
        );
    }


    @Test
    @DisplayName("Creates expense with debits")
    void create() throws Exception {
        // given
        ExpenseDto expenseDto = this.generateExpenseDto();

        String body = objectMapper.writeValueAsString(expenseDto);
        // when
        MvcResult mvcResult = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ExpenseDto actual = objectMapper.readValue(response.getContentAsString(), ExpenseDto.class);

        // then
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(actual).isEqualTo(expenseDto.withId(actual.id()))
        );
    }

    @Test
    @DisplayName("Negative test for creating expense")
    void create_shouldThrow() throws Exception {
        // given
        ExpenseDto expenseDto = this.generateWrongExpenseDto();

        String body = objectMapper.writeValueAsString(expenseDto);
        // when
        MvcResult mvcResult = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()),
            () -> assertThat(response.getContentType()).contains(MediaType.TEXT_PLAIN_VALUE),
            () -> assertThat(response.getContentAsString()).contains(
                "sum",
                "Title"
            )
        );
    }

    @Test
    @DisplayName("Update expense with debits")
    void update() throws Exception {
        // given
        ExpenseDto expenseDto = this.generateExpenseDto();

        String createExpenseBody = objectMapper.writeValueAsString(expenseDto);

        MvcResult createResult = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createExpenseBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();
        MockHttpServletResponse createResponse = createResult.getResponse();
        ExpenseDto createdExpenseDto = objectMapper.readValue(createResponse.getContentAsString(), ExpenseDto.class);

        // when
        ExpenseDto expectedExpenseDto = this.generateUpdatedExpenseDto();

        String updateExpenseBody = objectMapper.writeValueAsString(expectedExpenseDto);

        MvcResult mvcResult = mockMvc.perform(put(BASE_URI + "/" + createdExpenseDto.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateExpenseBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse updateResponse = mvcResult.getResponse();
        ExpenseDto actualExpenseDto = objectMapper.readValue(updateResponse.getContentAsString(), ExpenseDto.class);

        // then
        assertAll(
            () -> assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(updateResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(actualExpenseDto).isEqualTo(expectedExpenseDto.withId(createdExpenseDto.id()))
        );
    }

    @Test
    @DisplayName("Negative test for updating expense")
    void update_shouldThrow() throws Exception {
        // given
        ExpenseDto expenseDto = this.generateExpenseDto();

        String createExpenseBody = objectMapper.writeValueAsString(expenseDto);

        MvcResult createResult = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createExpenseBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();
        MockHttpServletResponse createResponse = createResult.getResponse();
        ExpenseDto createdExpenseDto = objectMapper.readValue(createResponse.getContentAsString(), ExpenseDto.class);

        // when
        ExpenseDto incorrectExpenseDto = this.generateWrongUpdatedExpenseDto();

        String incorrectUpdateExpenseBody = objectMapper.writeValueAsString(incorrectExpenseDto);

        MvcResult mvcResult = mockMvc.perform(put(BASE_URI + "/" + createdExpenseDto.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(incorrectUpdateExpenseBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse updateResponse = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()),
            () -> assertThat(updateResponse.getContentType()).contains(MediaType.TEXT_PLAIN_VALUE),
            () -> assertThat(updateResponse.getContentAsString()).contains(
                "amount",
                "positive"
            )
        );
    }

    @Test
    @DisplayName("Delete existing expense with debits")
    void deleteExpense() throws Exception {
        // given
        ExpenseDto expenseDto = this.generateExpenseDto();

        String createExpenseBody = objectMapper.writeValueAsString(expenseDto);

        MvcResult createResult = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createExpenseBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();
        MockHttpServletResponse createResponse = createResult.getResponse();
        ExpenseDto createdExpenseDto = objectMapper.readValue(createResponse.getContentAsString(), ExpenseDto.class);

        // when
        MvcResult deleteResult = mockMvc.perform(delete(BASE_URI + "/" + createdExpenseDto.id())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();
        MockHttpServletResponse deleteResultResponse = deleteResult.getResponse();

        // then
        assertAll(
            () -> assertThat(deleteResultResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @Test
    @DisplayName("Negative test for delete expense that does not exist")
    void deleteExpense_shouldThrow() throws Exception {
        // given
        long invalidExpenseId = -1L;

        // when
        MvcResult deleteResult = mockMvc.perform(delete(BASE_URI + "/" + invalidExpenseId)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();
        MockHttpServletResponse deleteResultResponse = deleteResult.getResponse();

        // then
        assertAll(
            () -> assertThat(deleteResultResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    private ExpenseDto generateWrongExpenseDto() {
        double totalAmount = 1000.0;
        List<UserListDto> flatUsers = this.generateListOfUserListDtoInFlat();
        List<DebitDto> debitDtos = this.generateDebitUsers(totalAmount + 10);

        return ExpenseDtoBuilder.builder()
            .description("Random desc")
            .amountInCents(totalAmount)
            .createdAt(LocalDateTime.now())
            .paidBy(flatUsers.get(0))
            .debitUsers(debitDtos)
            .build();
    }

    private ExpenseDto generateExpenseDto() {
        double totalAmount = 1000.0;
        List<UserListDto> flatUsers = this.generateListOfUserListDtoInFlat();
        List<DebitDto> debitDtos = this.generateDebitUsers(totalAmount);

        return ExpenseDtoBuilder.builder()
            .title("Next Expense")
            .description("Random desc")
            .amountInCents(totalAmount)
            .createdAt(LocalDateTime.now())
            .paidBy(flatUsers.get(0))
            .debitUsers(debitDtos)
            .isRepeating(false)
            .build();
    }

    private ExpenseDto generateUpdatedExpenseDto() {
        double totalAmount = 600.0;
        List<UserListDto> flatUsers = this.generateListOfUserListDtoInFlat();
        List<DebitDto> debitDtos = this.generateDebitUsers(totalAmount);

        return ExpenseDtoBuilder.builder()
            .title("Updated Expense")
            .description("This is an updated description")
            .amountInCents(totalAmount)
            .createdAt(LocalDateTime.now())
            .paidBy(flatUsers.get(0))
            .debitUsers(debitDtos)
            .isRepeating(false)
            .build();
    }

    private ExpenseDto generateWrongUpdatedExpenseDto() {
        double totalAmount = -1;
        List<UserListDto> flatUsers = this.generateListOfUserListDtoInFlat();
        List<DebitDto> debitDtos = this.generateDebitUsers(totalAmount);

        return ExpenseDtoBuilder.builder()
            .title("Updated Expense")
            .description("This is an updated description")
            .amountInCents(totalAmount)
            .createdAt(LocalDateTime.now())
            .paidBy(flatUsers.get(0))
            .debitUsers(debitDtos)
            .isRepeating(false)
            .build();
    }

    private List<UserListDto> generateListOfUserListDtoInFlat() {
        return applicationUser.getSharedFlat().getUsers().stream()
            .map(userMapper::entityToUserListDto).
            collect(Collectors.toList());
    }

    private List<DebitDto> generateDebitUsers(double amount) {
        List<UserListDto> flatUsers = this.generateListOfUserListDtoInFlat();

        return flatUsers.stream()
            .map((user) ->
                DebitDtoBuilder.builder()
                    .user(user)
                    .splitBy(SplitBy.EQUAL)
                    .value(amount / flatUsers.size())
                    .build()
            ).collect(Collectors.toList());
    }
}