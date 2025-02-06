package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
class DigitalStorageServiceTest {

    @Autowired
    private DigitalStorageService service;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedFlatService sharedFlatService;

    @Autowired
    private ItemService itemService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }


    @Test
    @DisplayName("If a storage exists it should be returned when searching storages by ID")
    void givenDigitalStorageIdWhenFindByIdThenDigitalStorageIsReturned() throws AuthorizationException {
        // given
        Long id = 1L;


        // when
        DigitalStorage actual = service.findById(id);

        // then
        assertAll(
            () -> assertThat(actual.getStorageId()).isEqualTo(id)
        );
    }

    @Test
    @DisplayName("If a storage does not exist then a not found exception should be thrown")
    void givenInvalidDigitalStorageIdWhenFindByIdThenNothingIsReturned() {
        // given
        Long id = -1L;

        // when + then
        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    @DisplayName("Using find all should return all storages of the currently active user")
    void givenNothingWhenFindAllThenAllDigitalStoragesOfActiveUserAreReturned() throws AuthorizationException {
        // when
        List<DigitalStorage> actual = service.findAll(null);

        // then
        assertThat(actual).hasSizeGreaterThanOrEqualTo(1);
    }


    @Test
    @Disabled("Test does not work, because it tries to create second digital storage for a WG, " +
        "but one WG can have only one DS. It is still here, because of the opportunity to extend " +
        "the functionality of the app. ")
    @DisplayName("It should be possible to create a second storage")
    void givenValidStorageWhenCreateThenStorageIsPersistedAndHasId() throws Exception {
        // given
        when(jwtTokenizer.getEmailFromToken(any(String.class))).thenReturn(applicationUser.getEmail());

        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setName("TestWG");
        sharedFlat.setPassword("1234");

        WgDetailDto wgDetailDto = sharedFlatService.create(sharedFlat);
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("MyTestStorage")
            .sharedFlat(wgDetailDto)
            .build();

        // when
        DigitalStorage actual = service.create(digitalStorageDto);

        // then
        DigitalStorage persisted = service.findById(actual.getStorageId());

        assertAll(
            () -> assertThat(actual).isEqualTo(persisted),
            () -> assertThat(actual.getTitle()).isEqualTo(digitalStorageDto.title())
        );
    }

    @Test
    @DisplayName("Creating an invalid storage should throw an exception")
    void givenInvalidStorageWhenCreateThenValidationExceptionIsThrown() {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("")
            .build();

        // when + then
        assertThrows(ValidationException.class, () -> service.create(digitalStorageDto));
    }

    @Test
    @Disabled
    @DisplayName("When searching a non-existent storage a exception should be thrown")
    void givenInvalidStorageWhenSearchItemsThenValidationExceptionIsThrown() {
        // given
        Long iD = -1111L;
        ItemSearchDto searchParams = new ItemSearchDto(null, null, null, null, null);

        // when + then
        assertThrows(ValidationException.class, () -> service.searchItems(searchParams));
    }

    @Test
    @DisplayName("When using valid search parameters the appropriate item should be returned")
    void givenValidSearchParamsWhenSearchItemsThenReturnList() throws ValidationException, AuthorizationException {
        // given
        ItemSearchDto searchParams = new ItemSearchDto(false, null, null, null, null);
        ItemListDto itemListDto = ItemListDtoBuilder.builder()
            .generalName("sweets")
            .quantityCurrent(100.0)
            .quantityTotal(100.0)
            .storageId(1L)
            .unit(UnitDtoBuilder.builder().name("g").build())
            .build();

        // when
        List<ItemListDto> result = service.searchItems(searchParams);

        // then
        assertAll(
            () -> assertThat(result).isNotEmpty(),
            () -> assertThat(result).contains(itemListDto)
        );
    }

    @Test
    @DisplayName("When using invalid search parameters then we should get a validation exception")
    void givenInvalidSearchParamsWhenSearchItemsThenThrowValidationException() {
        // given
        ItemSearchDto invalidSearchParams = new ItemSearchDto(null, null, null, null, null);

        // when + then
        assertThrows(ValidationException.class, () -> service.searchItems(invalidSearchParams));
    }

    private static Stream<Arguments> givenValidSearchParamsWhenSearchItemsThenReturnSortedListData() {

        return Stream.of(
            Arguments.of(
                ItemOrderType.GENERAL_NAME,
                false,
                List.of(
                    TestData.validInStockItemDto3.generalName(),
                    TestData.validInStockItemDto4.generalName(),
                    TestData.validInStockItemDto.generalName(),
                    TestData.validInStockItemDto2.generalName()
                )
            ),
            Arguments.of(
                ItemOrderType.GENERAL_NAME,
                true,
                List.of(
                    TestData.validInStockItemDto2.generalName(),
                    TestData.validInStockItemDto.generalName(),
                    TestData.validInStockItemDto4.generalName(),
                    TestData.validInStockItemDto3.generalName()
                )
            ),
            Arguments.of(
                ItemOrderType.QUANTITY_CURRENT,
                false,
                List.of(
                    TestData.validInStockItemDto3.generalName(),
                    TestData.validInStockItemDto4.generalName(),
                    TestData.validInStockItemDto.generalName(),
                    TestData.validInStockItemDto2.generalName()
                )
            ),
            Arguments.of(
                ItemOrderType.QUANTITY_CURRENT,
                true,
                List.of(
                    TestData.validInStockItemDto2.generalName(),
                    TestData.validInStockItemDto.generalName(),
                    TestData.validInStockItemDto4.generalName(),
                    TestData.validInStockItemDto3.generalName()
                )
            )

        );

    }

    @ParameterizedTest
    @DisplayName("Sorting by name")
    @MethodSource("givenValidSearchParamsWhenSearchItemsThenReturnSortedListData")
    void givenValidSearchParamsWhenSearchItemsThenReturnSortedList(ItemOrderType orderType,
                                                                   Boolean desc,
                                                                   List<String> expected) throws ValidationException, AuthorizationException, ConflictException {
        // given
        ItemSearchDto searchParams = new ItemSearchDto(false, null, null, orderType, desc);
        List<String> ids = new ArrayList<>();
        ids.add(itemService.create(TestData.validInStockItemDto).getItemCache().getGeneralName());
        ids.add(itemService.create(TestData.validInStockItemDto2).getItemCache().getGeneralName());
        ids.add(itemService.create(TestData.validInStockItemDto3).getItemCache().getGeneralName());
        ids.add(itemService.create(TestData.validInStockItemDto4).getItemCache().getGeneralName());

        // when
        List<ItemListDto> result = service.searchItems(searchParams);

        // then
        assertThat(result).isNotNull();
        assertAll(
            () -> assertThat(result).isNotEmpty(),
            () -> assertThat(result.stream()
                .map(ItemListDto::generalName).filter(ids::contains
                )
                .toList()
            ).containsExactlyElementsOf(expected)
        );
    }
}