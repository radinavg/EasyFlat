package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.CleanDatabase;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.IngredientsDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ItemLabelDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.SharedFlatDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.ShoppingListDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.StorageDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemCache;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ItemValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ShoppingItemValidator;
import com.github.javafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("unitTest")
public class ShoppingListServiceTest {

    @Autowired
    private ShoppingListServiceImpl shoppingListService;

    @Autowired
    private CleanDatabase cleanDatabase;

    @MockBean
    private AuthService authService;

    @MockBean
    private ShoppingItemValidator shoppingItemValidator;

    @MockBean
    private UnitService unitService;

    @MockBean
    private IngredientService ingredientService;

    @MockBean
    private ItemValidator itemValidator;

    @Autowired
    private StorageDataGenerator storageDataGenerator;

    @Autowired
    private IngredientsDataGenerator ingredientsDataGenerator;

    @Autowired
    private ShoppingListDataGenerator shoppingListDataGenerator;

    @Autowired
    private SharedFlatDataGenerator sharedFlatDataGenerator;

    @Autowired
    private ItemLabelDataGenerator itemLabelDataGenerator;

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    private LabelMapper labelMapper;

    private final Faker faker = new Faker(new Random(24012024));

    private ShoppingItemDto validShoppingItemDto;
    private ShoppingItem validShoppingItemEntity;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        cleanDatabase.truncateAllTablesAndRestartIds();

        sharedFlatDataGenerator.generateSharedFlats();
        shoppingListDataGenerator.generateShoppingLists();
        storageDataGenerator.generateDigitalStorages();
        ingredientsDataGenerator.generateIngredients();
        itemLabelDataGenerator.generateItemLabels();

        Unit testUnit1 = new Unit();
        testUnit1.setName("g");
        unitRepository.save(testUnit1);
        Unit testUnit2 = new Unit();
        testUnit2.setName("kg");
        testUnit2.setConvertFactor(1000L);
        testUnit2.setSubUnit(Set.of(testUnit1));
        unitRepository.save(testUnit2);

        UnitDto testUnitDto = UnitDtoBuilder.builder()
            .name("g")
            .subUnit(Set.of())
            .build();
        WgDetailDto sharedFlatDto = new WgDetailDto();
        sharedFlatDto.setId(1L);
        sharedFlatDto.setName("Shared Flat 1");
        DigitalStorageDto storageDto = DigitalStorageDtoBuilder.builder()
            .storageId(1L)
            .title("Storage 1")
            .sharedFlat(sharedFlatDto)
            .build();
        List<IngredientDto> ingredientDtoList = new ArrayList<>();
        ingredientDtoList.add(new IngredientDto(1L, "Ingredient 1"));
        ingredientDtoList.add(new IngredientDto(2L, "Ingredient 2"));
        ingredientDtoList.add(new IngredientDto(3L, "Ingredient 3"));
        List<ItemLabel> labels = labelRepository.findAll();

        String country = faker.country().name();
        validShoppingItemDto = ShoppingItemDtoBuilder.builder()
            .ean("1234567890123")
            .generalName("fruit")
            .productName("apple")
            .brand("clever")
            .quantityCurrent(3.0)
            .quantityTotal(3.0)
            .unit(testUnitDto)
            .description("Manufactured in " + country)
            .priceInCent(210L)
            .alwaysInStock(false)
            .boughtAt("billa")
            .ingredients(ingredientDtoList)
            .shoppingList(new ShoppingListDto(1L, "Shopping List (Default)", 0))
            .labels(labelMapper.itemLabelListToItemLabelDtoList(labels))
            .build();

        validShoppingItemEntity = new ShoppingItem();
        validShoppingItemEntity.setLabels(labels);
        validShoppingItemEntity.setBoughtAt("billa");
        validShoppingItemEntity.setAlwaysInStock(false);
        validShoppingItemEntity.setPriceInCent(210L);
        validShoppingItemEntity.setQuantityCurrent(3.0);
        ItemCache itemCache = getItemCache(country);
        validShoppingItemEntity.setItemCache(itemCache);
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(1L);
        shoppingList.setName("Shopping List (Default)");
        validShoppingItemEntity.setShoppingList(shoppingList);

        DigitalStorage digitalStorage = new DigitalStorage();
        digitalStorage.setStorageId(1L);
        SharedFlat sharedFlat = new SharedFlat().setId(1L);
        sharedFlat.setDigitalStorage(digitalStorage);

        ApplicationUser testUser = userRepository.save(new ApplicationUser(null, "User", "Userer", "user@email.com", "password", Boolean.FALSE, sharedFlat));
        when(authService.getUserFromToken()).thenReturn(testUser);
    }

    @Test
    void givenValidShoppingItemDtoWhenCreateShoppingItemShouldSucceed() throws ValidationException, ConflictException, AuthorizationException {
        // Mock the necessary method calls
        when(unitService.findAll()).thenReturn(Collections.emptyList());
        doNothing().when(shoppingItemValidator).validateForCreate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        // Act
        ShoppingItem result = shoppingListService.createShoppingItem(validShoppingItemDto);

        // Assert
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1L, result.getItemId()),
            () -> assertEquals(validShoppingItemDto.generalName(), result.getItemCache().getGeneralName()),
            () -> assertEquals(validShoppingItemDto.productName(), result.getItemCache().getProductName()),
            () -> assertEquals(validShoppingItemDto.ean(), result.getItemCache().getEan()),
            () -> assertEquals(validShoppingItemDto.brand(), result.getItemCache().getBrand()),
            () -> assertEquals(validShoppingItemDto.unit().name(), result.getItemCache().getUnit().getName()),
            () -> assertEquals(validShoppingItemDto.description(), result.getItemCache().getDescription()),
            () -> assertEquals(validShoppingItemDto.quantityTotal(), result.getItemCache().getQuantityTotal()),
            () -> assertEquals(validShoppingItemDto.quantityCurrent(), result.getQuantityCurrent()),
            () -> assertFalse(result.getAlwaysInStock()),
            () -> assertNull(result.getMinimumQuantity()),
            () -> assertEquals(validShoppingItemDto.boughtAt(), result.getBoughtAt()),
            () -> assertEquals(validShoppingItemDto.ingredients().size(), result.getItemCache().getIngredientList().size()),
            () -> assertEquals(validShoppingItemDto.shoppingList().id(), result.getShoppingList().getId()),
            () -> assertEquals(validShoppingItemDto.labels().size(), result.getLabels().size()),
            () -> assertEquals(validShoppingItemDto.labels().get(0).labelColour(), result.getLabels().get(0).getLabelColour())
        );
    }

    @Test
    void givenExistingShoppingItemWhenUpdateShoppingItemShouldSucceed() throws ValidationException, ConflictException, AuthorizationException {
        when(unitService.findAll()).thenReturn(Collections.emptyList());
        when(ingredientService.findIngredientsAndCreateMissing(any())).thenReturn(new ArrayList<>());
        doNothing().when(shoppingItemValidator).validateForUpdate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        // save shopping item to database
        shoppingItemRepository.save(new ShoppingItem());

        // update saved shopping item
        UnitDto testUnitDto = UnitDtoBuilder.builder()
            .name("kg")
            .subUnit(Set.of(UnitDtoBuilder.builder().name("g").build()))
            .convertFactor(1000L)
            .build();
        validShoppingItemDto = ShoppingItemDtoBuilder.builder()
            .itemId(1L)
            .ean("0234567890123")
            .generalName("vegetable")
            .productName("cucumber")
            .brand("smart spend")
            .quantityCurrent(2.0)
            .quantityTotal(2.0)
            .unit(testUnitDto)
            .description("Manufactured in " + faker.country().name())
            .priceInCent(210L)
            .alwaysInStock(true)
            .minimumQuantity(2.0)
            .boughtAt("spar")
            .shoppingList(new ShoppingListDto(2L, "Tech", 0))
            .build();
        ShoppingItem result = shoppingListService.updateShoppingItem(validShoppingItemDto);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1L, result.getItemId()),
            () -> assertEquals(validShoppingItemDto.generalName(), result.getItemCache().getGeneralName()),
            () -> assertEquals(validShoppingItemDto.productName(), result.getItemCache().getProductName()),
            () -> assertEquals(validShoppingItemDto.ean(), result.getItemCache().getEan()),
            () -> assertEquals(validShoppingItemDto.brand(), result.getItemCache().getBrand()),
            () -> assertEquals(validShoppingItemDto.unit().name(), result.getItemCache().getUnit().getName()),
            () -> {
                assert validShoppingItemDto.unit().subUnit() != null;
                assertEquals(validShoppingItemDto.unit().subUnit().size(), result.getItemCache().getUnit().getSubUnit().size());
            },
            () -> assertEquals(validShoppingItemDto.unit().convertFactor(), result.getItemCache().getUnit().getConvertFactor()),
            () -> assertEquals(validShoppingItemDto.description(), result.getItemCache().getDescription()),
            () -> assertEquals(validShoppingItemDto.quantityTotal(), result.getItemCache().getQuantityTotal()),
            () -> assertEquals(validShoppingItemDto.quantityCurrent(), result.getQuantityCurrent()),
            () -> assertTrue(result.getAlwaysInStock()),
            () -> assertEquals(validShoppingItemDto.minimumQuantity(), result.getMinimumQuantity()),
            () -> assertEquals(validShoppingItemDto.boughtAt(), result.getBoughtAt()),
            // updated shopping item has no ingredients
            () -> assertEquals(0, result.getItemCache().getIngredientList().size()),
            () -> assertEquals(validShoppingItemDto.shoppingList().id(), result.getShoppingList().getId()),
            () -> assertNull(result.getLabels())
        );
    }

    @Test
    void givenNonExistingShoppingItemWhenUpdateShoppingItemShouldThrowNotFoundException() throws ConflictException, ValidationException {
        when(unitService.findAll()).thenReturn(Collections.emptyList());
        when(ingredientService.findIngredientsAndCreateMissing(any())).thenReturn(new ArrayList<>());
        doNothing().when(shoppingItemValidator).validateForUpdate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        ShoppingItemDto nonExistingShoppingItemDto = ShoppingItemDtoBuilder.builder()
            .itemId(-1L).build();

        assertThrows(NotFoundException.class, () -> shoppingListService.updateShoppingItem(nonExistingShoppingItemDto));

    }

    @Test
    void givenNonExistingShoppingListWhenGetItemsByShoppingListIdShouldThrowNotFoundException() {
        Long idOfNonExistingShoppingList = -1L;

        assertThrows(NotFoundException.class, () -> shoppingListService.getItemsByShoppingListId(idOfNonExistingShoppingList, new ShoppingItemSearchDto(null, null, null)));
    }

    @Test
    void givenUnauthorizedUserWhenGetItemsByShoppingListIdShouldThrowAuthorizationException() {
        // save new user linked to shared flat with Id 2
        ApplicationUser testUser = userRepository.save(new ApplicationUser(null, "User1", "Userer1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(2L)));
        when(authService.getUserFromToken()).thenReturn(testUser);

        Long idOfExistingShoppingList = 1L; // is linked to SharedFlat with Id 1

        assertThrows(AuthorizationException.class, () -> shoppingListService.getItemsByShoppingListId(idOfExistingShoppingList, new ShoppingItemSearchDto(null, null, null)));
    }

    @Test
    void givenUnauthorizedUserWhenDeleteItemShouldThrowAuthorizationException() {
        // save new user linked to SharedFlat with Id 2
        ApplicationUser testUser = userRepository.save(new ApplicationUser(null, "User1", "Userer1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(2L)));
        when(authService.getUserFromToken()).thenReturn(testUser);

        // save ShoppingItem with ShoppingList linked to SharedFlat with Id 1
        ShoppingList existingShoppingList = shoppingListRepository.findById(1L).get();
        ShoppingItem toSave = new ShoppingItem();
        toSave.setShoppingList(existingShoppingList);
        ShoppingItem saved = shoppingItemRepository.save(toSave);

        assertThrows(AuthorizationException.class, () -> shoppingListService.deleteItem(saved.getItemId()));
    }

    @Test
    void givenExistingShoppingItemWhenDeleteItemShouldSucceed() throws AuthorizationException, ConflictException {
        ShoppingItem toDelete = shoppingItemRepository.save(validShoppingItemEntity);

        ShoppingItem deleted = shoppingListService.deleteItem(toDelete.getItemId());

        assertAll(
            () -> assertEquals(toDelete.getItemId(), deleted.getItemId()),
            () -> assertEquals(validShoppingItemEntity.getItemCache(), deleted.getItemCache()),
            () -> assertEquals(validShoppingItemEntity.getShoppingList().getId(), deleted.getShoppingList().getId()),
            // references to existing labels should be removed
            () -> assertNotNull(deleted.getLabels()),
            () -> assertEquals(validShoppingItemEntity.getBoughtAt(), deleted.getBoughtAt()),
            () -> assertEquals(validShoppingItemEntity.getQuantityCurrent(), deleted.getQuantityCurrent()),
            () -> assertEquals(validShoppingItemEntity.getAlwaysInStock(), deleted.getAlwaysInStock()),
            () -> assertEquals(validShoppingItemEntity.getPriceInCent(), deleted.getPriceInCent()),
            () -> assertNull(deleted.getMinimumQuantity())
        );
    }

    @Test
    void givenExistingShoppingListsGetShoppingListsShouldSucceed() throws AuthorizationException {
        // save new user linked to SharedFlat with Id 2
        ApplicationUser testUser = userRepository.save(new ApplicationUser(null, "User1", "Userer1", "user1@email.com", "password", Boolean.FALSE, new SharedFlat().setId(1L)));
        when(authService.getUserFromToken()).thenReturn(testUser);

        List<ShoppingList> result = shoppingListService.getShoppingLists("");

        List<ShoppingList> existingLists = shoppingListRepository.findBySharedFlat(new SharedFlat().setId(1L));
        assertAll(
            () -> assertEquals(existingLists.size(), result.size()),
            () -> {
                for (int i = 0; i < existingLists.size(); i++) {
                    assertEquals(existingLists.get(i).getId(), result.get(i).getId());
                }
            }
        );
    }

    @Test
    void givenExistingShoppingListDeleteListShouldSucceed() throws ValidationException, AuthorizationException, ConflictException {
        // add some ShoppingItems to the existing ShoppingList in SharedFlat with Id 1
        ShoppingList toDelete = shoppingListRepository.findById(2L).get();

        List<ShoppingItem> items = new ArrayList<>();
        validShoppingItemEntity.setShoppingList(toDelete);
        ShoppingItem item2 = new ShoppingItem();
        item2.setShoppingList(toDelete);
        items.add(shoppingItemRepository.save(validShoppingItemEntity)); // item referencing other existing objects
        items.add(shoppingItemRepository.save(item2)); // item not referencing other objects except for ShoppingList toDelete
        toDelete.setItems(items);
        shoppingListRepository.save(toDelete);

        ShoppingList result = shoppingListService.deleteList(toDelete.getId());

        assertAll(
            () -> assertEquals(toDelete.getId(), result.getId()),
            () -> assertEquals(toDelete.getName(), result.getName()),
            // items associated with deleted ShoppingList should also be deleted
            () -> assertEquals(0, shoppingItemRepository.findByShoppingListId(toDelete.getId()).size())
        );
    }

    @Test
    void givenExistingShoppingItemAndExistingDigitalStorageTransferToServerShouldSucceed() throws AuthorizationException, ValidationException, ConflictException {
        doNothing().when(itemValidator).validateForCreate(any(), any(), any(), any());

        Long idOfExistingDigitalStorage = 1L; // linked to SharedFlat with Id 1

        // save ShoppingItem linked to SharedFlat with Id 1, DigitalStorage with Id 1 and ShoppingList with Id 1
        shoppingItemRepository.save(validShoppingItemEntity);

        List<DigitalStorageItem> result = shoppingListService.transferToServer(List.of(validShoppingItemDto.withId(1)));

        assertAll(
            // ShoppingList with Id 1 shouldn't be linked to any ShoppingItems
            () -> assertEquals(0, shoppingItemRepository.findByShoppingListId(1L).size()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).size(), 1),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(0).getItemCache().getProductName(), result.get(0).getItemCache().getProductName()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(0).getItemCache().getGeneralName(), result.get(0).getItemCache().getGeneralName()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(0).getItemCache().getEan(), result.get(0).getItemCache().getEan())
        );
    }

    @Test
    @DisplayName("Save one not and one always in stock item to storage")
    void givenTwoExistingShoppingItemAndExistingDigitalStorageTransferToServerShouldSucceed() throws AuthorizationException, ValidationException, ConflictException {
        //doNothing().when(itemValidator).validateForCreate(any(), any(), any(), any());

        Long idOfExistingDigitalStorage = 1L; // linked to SharedFlat with Id 1

        // save ShoppingItems linked to SharedFlat with Id 1 and ShoppingList with Id 1
        shoppingItemRepository.save(validShoppingItemEntity);

        validShoppingItemEntity.setItemId(null);
        validShoppingItemEntity.setItemCache(getItemCache("Bulgaria"));
        validShoppingItemEntity.setAlwaysInStock(true); // always in stock item
        shoppingItemRepository.save(validShoppingItemEntity);

        List<DigitalStorageItem> result = shoppingListService.transferToServer(List.of(validShoppingItemDto.withId(1), validShoppingItemDto.withAlwaysInStock(2, true)));

        assertAll(
            // ShoppingList with Id 1 shouldn't be linked to any ShoppingItems
            () -> assertEquals(0, shoppingItemRepository.findByShoppingListId(1L).size()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).size(), 2),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(0).getItemCache().getProductName(), result.get(0).getItemCache().getProductName()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(0).getItemCache().getGeneralName(), result.get(0).getItemCache().getGeneralName()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(0).getItemCache().getEan(), result.get(0).getItemCache().getEan()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(1).getItemCache().getProductName(), result.get(1).getItemCache().getProductName()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(1).getItemCache().getGeneralName(), result.get(1).getItemCache().getGeneralName()),
            () -> assertEquals(itemRepository.findAllByDigitalStorage_StorageId(idOfExistingDigitalStorage).get(1).getItemCache().getEan(), result.get(1).getItemCache().getEan())
        );
    }

    @Test
    void findDefaultShoppingListWhenGetShoppingListByNameShouldSucceed() {
        Optional<ShoppingList> result = shoppingListService.getShoppingListByName("Shopping List (Default)");

        assertAll(
            () -> assertTrue(result.isPresent()),
            () -> assertEquals("Shopping List (Default)", result.get().getName())
        );

    }

    @NotNull
    private static ItemCache getItemCache(String country) {
        ItemCache itemCache = new ItemCache();
        itemCache.setProductName("apple");
        itemCache.setGeneralName("fruit");
        Unit unit = new Unit();
        unit.setName("g");
        itemCache.setUnit(unit);
        itemCache.setEan("1234567890123");
        itemCache.setBrand("clever");
        itemCache.setDescription("Manufactured in " + country);
        itemCache.setQuantityTotal(3.0);
        List<Ingredient> ingredientEntityList = new ArrayList<>();
        Ingredient ingr1 = new Ingredient();
        ingr1.setIngrId(1L);
        Ingredient ingr2 = new Ingredient();
        ingr2.setIngrId(2L);
        Ingredient ingr3 = new Ingredient();
        ingr3.setIngrId(3L);
        ingredientEntityList.add(ingr1);
        ingredientEntityList.add(ingr2);
        ingredientEntityList.add(ingr3);
        itemCache.setIngredientList(ingredientEntityList);
        return itemCache;
    }

}
