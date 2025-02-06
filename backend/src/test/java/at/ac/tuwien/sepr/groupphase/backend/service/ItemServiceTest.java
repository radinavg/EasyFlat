package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.digitalStorageDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.g;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ingredientDtoList;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidAlwaysInStockItem;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidItemId;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.itemDtoWithInvalidDigitalStorage;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.itemDtoWithInvalidIngredients;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ml;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validAlwaysInStockItem;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validInStockItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validItemId;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataConverter.convertToAlwaysInStockItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataConverter.updateProductName;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataConverter.updateUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceTest {

    @Autowired
    private ItemService service;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private AuthService authService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Finding item with valid ID should return the item if it belongs to the current user")
    void givenItemIdWhenFindByIdThenItemIsReturned() throws AuthorizationException {
        // given
        // when
        DigitalStorageItem actual = service.findById(validItemId);

        // then
        assertThat(actual.getItemId()).isEqualTo(validItemId);
    }

    @Test
    @DisplayName("Finding item with invalid ID should return a not found exception")
    void givenInvalidItemIdWhenFindByIdThenNoItem() {
        // given
        // when + then
        assertThrows(NotFoundException.class, () -> service.findById(invalidItemId));
    }

    @Test
    @DisplayName("Find all items with a given general name")
    void givenGeneralNameWhenFindByFieldsThenItemWithGeneralNameIsReturned() {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
            .generalName("noodles")
            .build();

        // when
        List<DigitalStorageItem> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
            assertThat(item.getItemCache().getGeneralName()).containsSequence(itemFieldSearchDto.generalName())
        );
    }

    @Test
    @DisplayName("Searching for item brand should return all items that have this brand")
    void givenBrandWhenFindByFieldsThenItemWithBrandIsReturned() {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
            .brand("alpro")
            .build();

        // when
        List<DigitalStorageItem> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
            assertThat(item.getItemCache().getBrand()).containsSequence(itemFieldSearchDto.brand())
        );
    }

    @Test
    @DisplayName("Searching for item store should return all items that were bought at this store")
    void givenBoughtAtWhenFindByFieldsThenItemWithBoughtAtIsReturned() {
        // given
        ItemFieldSearchDto itemFieldSearchDto = ItemFieldSearchDtoBuilder.builder()
            .boughtAt("Billa")
            .build();

        // when
        List<DigitalStorageItem> actual = service.findByFields(itemFieldSearchDto);

        // then
        assertThat(actual).isNotEmpty();
        actual.forEach(item ->
            assertThat(item.getBoughtAt()).containsSequence(itemFieldSearchDto.boughtAt())
        );
    }

    @Test
    @DisplayName("It is possible to create an in-stock item using valid values")
    void givenValidItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthorizationException {
        // given

        // when
        DigitalStorageItem actual = service.create(validItemDto);

        // then
        DigitalStorageItem persisted = service.findById(actual.getItemId());

        assertThat(actual).isEqualTo(persisted);
        assertThat(actual)
            .extracting(
                (item) -> item.getItemCache().getEan(),
                (item) -> item.getItemCache().getGeneralName(),
                (item) -> item.getItemCache().getProductName(),
                (item) -> item.getItemCache().getBrand(),
                DigitalStorageItem::getQuantityCurrent,
                (item) -> item.getItemCache().getQuantityTotal(),
                (item) -> item.getItemCache().getUnit().getName(),
                DigitalStorageItem::getExpireDate,
                (item) -> item.getItemCache().getDescription(),
                DigitalStorageItem::getPriceInCent
            )
            .containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit().name(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent()
            );
        assertThat(actual.getDigitalStorage().getStorageId()).isEqualTo(validItemDto.digitalStorage().storageId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getTitle)
            .toList()
        ).isEqualTo(validItemDto.ingredients().stream().map(IngredientDto::name).toList());
    }

    @Test
    @DisplayName("It is possible to create an always-in-stock item using valid values")
    void givenValidAlwaysInStockItemWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthorizationException {
        // given
        // when
        DigitalStorageItem actual = service.create(validAlwaysInStockItem);

        // then
        DigitalStorageItem persisted = service.findById(actual.getItemId());

        assertThat(actual).isEqualTo(persisted);
        assertThat(actual)
            .extracting(
                (item) -> item.getItemCache().getEan(),
                (item) -> item.getItemCache().getGeneralName(),
                (item) -> item.getItemCache().getProductName(),
                (item) -> item.getItemCache().getBrand(),
                DigitalStorageItem::getQuantityCurrent,
                (item) -> item.getItemCache().getQuantityTotal(),
                (item) -> item.getItemCache().getUnit().getName(),
                DigitalStorageItem::getExpireDate,
                (item) -> item.getItemCache().getDescription(),
                DigitalStorageItem::getPriceInCent,
                DigitalStorageItem::alwaysInStock,
                DigitalStorageItem::getMinimumQuantity,
                DigitalStorageItem::getBoughtAt
            )
            .containsExactly(
                validAlwaysInStockItem.ean(),
                validAlwaysInStockItem.generalName(),
                validAlwaysInStockItem.productName(),
                validAlwaysInStockItem.brand(),
                validAlwaysInStockItem.quantityCurrent(),
                validAlwaysInStockItem.quantityTotal(),
                validAlwaysInStockItem.unit().name(),
                validAlwaysInStockItem.expireDate(),
                validAlwaysInStockItem.description(),
                validAlwaysInStockItem.priceInCent(),
                validAlwaysInStockItem.alwaysInStock(),
                validAlwaysInStockItem.minimumQuantity(),
                validAlwaysInStockItem.boughtAt()
            );
        assertThat(actual.getDigitalStorage().getStorageId()).isEqualTo(validAlwaysInStockItem.digitalStorage().storageId());
        assertThat(actual.getIngredientList().stream()
            .map(Ingredient::getTitle)
            .toList()
        ).isEqualTo(validAlwaysInStockItem.ingredients().stream().map(IngredientDto::name).toList());
    }

    @Test
    @DisplayName("It is not possible to create an in-stock item using invalid values")
    void givenInvalidItemWhenCreateThenValidationExceptionIsThrown() {
        // given
        // when + then
        String message = assertThrows(ValidationException.class, () -> service.create(invalidItemDto)).getMessage();
        assertThat(message)
            .contains(
                "quantity",
                "category",
                "product name",
                "EAN",
                "13",
                "price",
                "total"
            );
    }

    @Test
    @DisplayName("It is not possible to create an always-in-stock item using invalid values")
    void givenInvalidAlwaysInStockItemWhenCreateThenValidationExceptionIsThrown() {
        // given
        // when + then
        String message = assertThrows(ValidationException.class, () -> service.create(invalidAlwaysInStockItem)).getMessage();
        assertThat(message)
            .contains(
                "minimum quantity"
            );
    }

    @Test
    @DisplayName("It is not possible to create an item for an invalid storage")
    void givenItemWithInvalidStorageWhenCreateThenConflictExceptionIsThrown() {
        // given
        // when + then
        String message = assertThrows(ConflictException.class, () -> service.create(itemDtoWithInvalidDigitalStorage)).getMessage();
        assertThat(message).isNotEmpty();
        assertThat(message)
            .contains(
                "Digital Storage",
                "not exists"
            );
    }

    @Test
    @DisplayName("It is possible to update an item using a valid value")
    void givenValidItemWhenUpdateSingleAttributeThenItemIsUpdated() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        String updatedGeneralName = "General Name Updated";

        DigitalStorageItem createdDigitalStorageItem = service.create(validItemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName(updatedGeneralName)
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when:
        service.update(updatedItemDto);

        // then:
        DigitalStorageItem updatedItem = service.findById(createdDigitalStorageItem.getItemId());

        assertEquals(updatedGeneralName, updatedItem.getItemCache().getGeneralName());

    }

    @Test
    @DisplayName("It is not possible to update an item using an invalid value")
    void givenInvalidItemWhenUpdateSingleAttributeThenValidationExceptionIsThrown() throws ValidationException, ConflictException, AuthorizationException {
        // given:

        DigitalStorageItem createdDigitalStorageItem = service.create(validItemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(-100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.update(updatedItemDto)).getMessage();
        assertThat(message)
            .contains(
                "current quantity",
                "0"
            );
    }

    @Test
    @DisplayName("It is possible to update multiple fields of an item using valid values")
    void givenValidItemWhenUpdateMultipleAttributesThenItemIsUpdated() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        String updatedGeneralName = "General Name Updated";
        Double updatedCurrentAmount = 150.0;

        DigitalStorageItem createdDigitalStorageItem = service.create(validItemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName(updatedGeneralName)
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(updatedCurrentAmount)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when:
        service.update(updatedItemDto);

        // then:
        DigitalStorageItem updatedItem = service.findById(createdDigitalStorageItem.getItemId());

        assertAll(
            () -> assertEquals(updatedGeneralName, updatedItem.getItemCache().getGeneralName()),
            () -> assertEquals(updatedCurrentAmount, updatedItem.getQuantityCurrent())
        );
    }

    @Test
    @DisplayName("It is not possible to update multiple fields of an item using invalid values")
    void givenInvalidItemWhenUpdateMultipleAttributesThenValidationExceptionIsThrown() throws ValidationException, ConflictException, AuthorizationException {
        // given:
        DigitalStorageItem createdDigitalStorageItem = service.create(validItemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName("TestGeneral")
            .productName("TestProduct")
            .brand(null)
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(null)
            .ingredients(ingredientDtoList)
            .build();

        // when + then
        String message = assertThrows(ValidationException.class, () -> service.update(updatedItemDto)).getMessage();
        assertThat(message)
            .contains(
                "storage"
            );
    }

    @Test
    @DisplayName("It is possible to delete an item")
    void givenValidItemWhenDeleteThenItemIsDeleted() throws ValidationException, ConflictException, AuthorizationException {
        // given:

        DigitalStorageItem createdDigitalStorageItem = service.create(validItemDto);

        // when:
        service.delete(createdDigitalStorageItem.getItemId());

        // then:
        assertThrows(NotFoundException.class, () -> service.findById(createdDigitalStorageItem.getItemId()));
    }

    @Test
    @DisplayName("Items should be retrievable using their general name")
    void givenValidSearchParamsWhenGetItemsWithGeneralNameThenReturnList() {
        // given
        String itemName = "spreads";

        // when
        List<DigitalStorageItem> result = service.getItemWithGeneralName(itemName);


        // then
        assertAll(
            () -> assertThat(result).isNotEmpty(),
            () -> assertThat(result).isNotNull(),
            () -> assertEquals(result.size(), 1)
        );
    }

    @Test
    @DisplayName("Move Item from InStock to AlwaysInStock, and then the same Item back to Instock - Refs: #339")
    public void moveItemsBetweenInSockAndAlwaysInStock() throws ValidationException, AuthorizationException, ConflictException {
        // given
        DigitalStorageItem item = service.create(validInStockItemDto);

        // when + then
        ItemDto toAlwaysInStockItemDto = convertToAlwaysInStockItemDto(
            validInStockItemDto.withId(item.getItemId()),
            10L
        );

        assertDoesNotThrow(() -> {
                DigitalStorageItem updatedItem = service.update(toAlwaysInStockItemDto);
                service.update(validInStockItemDto.withId(updatedItem.getItemId()));
            }
        );
    }

    @Test
    @DisplayName("Create two itemes with same general name and different units - Refs: #253")
    public void createTwoItemsWithSameGeneralNameAndDifferentUnits() throws ValidationException, AuthorizationException, ConflictException {
        // given
        ItemDto itemDtoWithDifferentUnit = updateUnit(
            validInStockItemDto,
            UnitDtoBuilder.builder().name("g").build()
        );
        DigitalStorageItem createdDigitalStorageItem = service.create(validInStockItemDto);

        // when + then
        String errorMessage = assertThrows(ConflictException.class, () ->
            service.create(itemDtoWithDifferentUnit)
        ).getMessage();

        assertThat(errorMessage).containsSubsequence("unit");
    }

    @Test
    @DisplayName("Update Item within same general name to different unit - Refs: #253")
    public void updateItemWithSameGeneralNameToDifferentUnit() throws ValidationException, AuthorizationException, ConflictException {
        // given
        DigitalStorageItem createdDigitalStorageItem = service.create(validInStockItemDto);
        DigitalStorageItem createdDigitalStorageItem2 = service.create(updateProductName(validInStockItemDto, "Test2"));

        ItemDto itemDtoWithDifferentUnit = updateUnit(
            validInStockItemDto.withId(createdDigitalStorageItem.getItemId()),
            UnitDtoBuilder.builder().name("g").build()
        );

        // when + then
        String errorMessage = assertThrows(ConflictException.class, () ->
            service.update(itemDtoWithDifferentUnit)
        ).getMessage();

        assertThat(errorMessage).containsSubsequence("unit");
    }

    @Test
    @DisplayName("If an in-stock item quantity is updated to zero the item should be deleted")
    public void givenValidItemWhenUpdateItemToZeroThenItemIsDeleted() throws ValidationException, AuthorizationException, ConflictException {
        // given
        Double updatedCurrentAmount = 0.0;

        DigitalStorageItem inStockItem = service.create(validItemDto);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(inStockItem.getItemId())
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(updatedCurrentAmount)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .boughtAt("Hofer")
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        // when
        service.update(updatedItemDto);

        // then
        assertThrows(NotFoundException.class, () -> service.findById(inStockItem.getItemId()));
    }

    @Test
    @DisplayName("If an always-in-stock item quantity is updated to zero the item should not be deleted")
    public void givenValidAlwaysInStockItemWhenUpdateItemToZeroThenItemIsNotDeleted() throws ValidationException, AuthorizationException, ConflictException {
        // given
        Double updatedCurrentAmount = 0.0;

        DigitalStorageItem alwaysInStockItem = service.create(validAlwaysInStockItem);

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(alwaysInStockItem.getItemId())
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(updatedCurrentAmount)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .alwaysInStock(true)
            .minimumQuantity(100L)
            .boughtAt("Hofer")
            .build();

        // when
        service.update(updatedItemDto);

        // then
        assertDoesNotThrow(
            () -> service.findById(alwaysInStockItem.getItemId())
        );
    }

    @Test
    @DisplayName("Try to create item with invalid ingredients - Refs: #288")
    void givenItemWithInvalidIngredientWhenCreateThenItemIsPersistedWithId() throws ValidationException, ConflictException, AuthorizationException {
        // given

        // when
        String message = assertThrows(ValidationException.class, () ->
            service.create(itemDtoWithInvalidIngredients)
        ).getMessage();

        // then
        List<Ingredient> allIngredients = ingredientRepository.findAll();

        assertAll(
            () -> assertThat(message).containsSequence("ingredient"),
            () -> assertThat(allIngredients).extracting(
                Ingredient::getTitle
            ).doesNotContain(
                itemDtoWithInvalidIngredients.ingredients().get(0).name(),
                itemDtoWithInvalidIngredients.ingredients().get(1).name()
            )
        );


    }

    @Test
    @DisplayName("Test matching recipe ingredient to digital storage item")
    void testMatchRecipeIngredientToDigitalStorageItem()
        throws AuthorizationException, ValidationException, ConflictException {
        DigitalStorageItem createdDigitalStorageItem = service.create(validInStockItemDto);

        AlternativeNameDto alternativeName = AlternativeNameDtoBuilder.builder().name("apple").build();
        AlternativeName alternativeNameEntity = new AlternativeName();
        alternativeNameEntity.setName("apple");

        ItemDto updatedItemDto = ItemDtoBuilder.builder()
            .itemId(createdDigitalStorageItem.getItemId())
            .ean("0123456789123")
            .generalName("Test")
            .productName("TestProduct")
            .brand("TestBrand")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(g)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .alternativeNames(List.of(alternativeName))
            .build();

        //when

        DigitalStorageItem updatedItem = service.update(updatedItemDto);

        assertThat(updatedItem.getItemCache().getAlternativeNames()).contains(alternativeNameEntity);

    }

    @Test
    @DisplayName("Check if findAll delivers all items without limit")
    public void givenNoLimitWhenFindAllThenAllItemsAreReturned() {
        // given
        // when
        List<DigitalStorageItem> result = service.findAll(0);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(itemRepository.findAll().size());
    }

    @Test
    @DisplayName("Check if findAll delivers all items with limit")
    public void givenLimitWhenFindAllThenAllItemsAreReturned() {
        // given
        // when
        List<DigitalStorageItem> result = service.findAll(5);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Given valid item name, then findByName returns valid items")
    void givenValidItemNameThanFindByNameReturnsValidItems() {

        List<DigitalStorageItem> result = service.findAll(1);
        DigitalStorageItem resItem = result.get(0);
        String itemName = resItem.getItemCache().getProductName();
        String unitName = resItem.getItemCache().getUnit().getName();
        //when
        List<DigitalStorageItem> foundItems = service.findByName(itemName,unitName);

        //then
        assertAll(
            () -> assertThat(foundItems).isNotEmpty(),
            () -> assertThat(foundItems.get(0).getItemCache().getProductName()).isEqualTo(itemName),
            () -> assertThat(foundItems.get(0).getItemCache().getUnit().getName()).isEqualTo(unitName)
        );

    }

    @Test
    @DisplayName("Given invalid item name, then findByName returns empty list")
    void givenInValidItemNameThanFindByNameReturnsEmptyList() {
        String itemName = "Invalid Product Name";
        String unitName = "pcs";

        //when
        List<DigitalStorageItem> foundItems = service.findByName(itemName, unitName);

        //then
        assertThat(foundItems).isEmpty();
    }


}