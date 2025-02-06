package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ItemLabelValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ShoppingItemValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingListValidatorImpl;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingItemRepository shoppingItemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;
    private final LabelService labelService;
    private final ItemMapper itemMapper;

    private final ItemService itemService;
    private final IngredientMapper ingredientMapper;
    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final IngredientService ingredientService;
    private final DigitalStorageRepository digitalStorageRepository;
    private final ShoppingItemValidator shoppingItemValidator;
    private final UnitService unitService;
    private final ShoppingListValidatorImpl shoppingListValidator;
    private final AuthService authService;
    private final ItemLabelValidator itemLabelValidator;

    public ShoppingListServiceImpl(ShoppingItemRepository shoppingItemRepository, ShoppingListRepository shoppingListRepository,
                                   ShoppingListMapper shoppingListMapper, LabelService labelService, ItemMapper itemMapper,
                                   ItemService itemService, IngredientMapper ingredientMapper, ItemRepository itemRepository, DigitalStorageService digitalStorageService,
                                   IngredientService ingredientService, DigitalStorageRepository digitalStorageRepository,
                                   ShoppingItemValidator shoppingItemValidator, UnitService unitService, ShoppingListValidatorImpl shoppingListValidator, AuthService authService, ItemLabelValidator itemLabelValidator) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListMapper = shoppingListMapper;
        this.itemService = itemService;
        this.ingredientMapper = ingredientMapper;
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.digitalStorageRepository = digitalStorageRepository;
        this.shoppingItemValidator = shoppingItemValidator;
        this.unitService = unitService;
        this.shoppingListValidator = shoppingListValidator;
        this.authService = authService;
        this.itemLabelValidator = itemLabelValidator;
    }

    @Override
    @Transactional
    public ShoppingItem createShoppingItem(ShoppingItemDto itemDto)
        throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.trace("createShoppingItem({})", itemDto);
        List<ShoppingList> shoppingLists = this.getShoppingLists("");
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        shoppingItemValidator.validateForCreate(itemDto, shoppingLists, digitalStorageList, unitList);

        List<ItemLabel> labels = findLabelsAndCreateMissing(itemDto.labels());

        ShoppingItem si = itemMapper.shoppingItemDtoToShoppingItemEntity(itemDto, labels);

        return shoppingItemRepository.save(si);
    }

    @Override
    public Optional<ShoppingItem> getShoppingItemById(Long itemId) throws AuthorizationException {
        LOGGER.trace("getById({})", itemId);
        ApplicationUser applicationUser = authService.getUserFromToken();

        if (itemId == null) {
            return Optional.empty();
        }
        Optional<ShoppingItem> itemOptional = shoppingItemRepository.findById(itemId);
        if (itemOptional.isPresent()) {
            ShoppingItem item = itemOptional.get();
            if (!item.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                throw new AuthorizationException("Authorization failed", List.of("User has no access to this shopping item!"));
            }
        }

        return shoppingItemRepository.findById(itemId);
    }

    @Override
    public Optional<ShoppingList> getShoppingListByName(String name) {
        LOGGER.trace("getShoppingListByName({})", name);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return shoppingListRepository.getByNameAndSharedFlatIs(name, applicationUser.getSharedFlat());
    }

    @Override
    public Optional<ShoppingList> getShoppingListById(Long id) {
        LOGGER.trace("getShoppingListById({})", id);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (id == null) {
            return Optional.empty();
        }
        return shoppingListRepository.getByIdAndSharedFlatIs(id, applicationUser.getSharedFlat());
    }

    @Override
    public List<ShoppingItem> getItemsByShoppingListId(Long shopListId, ShoppingItemSearchDto itemSearchDto) throws AuthorizationException {
        LOGGER.trace("getItemsById({},{})", shopListId, itemSearchDto);
        ApplicationUser applicationUser = authService.getUserFromToken();
        Optional<ShoppingList> shoppingListOptional = shoppingListRepository.findById(shopListId);
        if (shoppingListOptional.isPresent()) {
            if (applicationUser.getSharedFlat() == null || !applicationUser.getSharedFlat().equals(shoppingListOptional.get().getSharedFlat())) {
                throw new AuthorizationException("Authorization failed", List.of("User has no access to this shopping list!"));
            }
        } else {
            throw new NotFoundException("Shopping list does not exist in the database!");
        }

        return shoppingItemRepository.searchItems(shopListId,
            (itemSearchDto.productName() != null) ? itemSearchDto.productName() : null,
            (itemSearchDto.label() != null) ? itemSearchDto.label() : null);
    }

    @Override
    @Transactional
    public ShoppingList createList(ShoppingListDto shoppingListDto) throws ValidationException, ConflictException {
        LOGGER.trace("createList({})", shoppingListDto);
        shoppingListValidator.validateForCreate(shoppingListDto);
        ApplicationUser applicationUser = authService.getUserFromToken();
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(shoppingListDto.name());
        shoppingList.setSharedFlat(applicationUser.getSharedFlat());
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    @Transactional
    public ShoppingItem deleteItem(Long itemId) throws AuthorizationException, ConflictException {
        LOGGER.trace("deleteItem({})", itemId);
        ApplicationUser applicationUser = authService.getUserFromToken();

        Optional<ShoppingItem> toDeleteOptional = shoppingItemRepository.findById(itemId);
        if (toDeleteOptional.isPresent()) {
            ShoppingItem toDelete = toDeleteOptional.get();
            // Enhanced authorization
            if (!toDelete.getShoppingList().getSharedFlat().equals(applicationUser.getSharedFlat())) {
                throw new AuthorizationException("Authorization failed", List.of("User has no access to this shopping item and can not delete it!"));
            }
            shoppingItemRepository.delete(toDelete);
            return toDelete;
        } else {
            throw new NotFoundException("Shopping item with this id does not exist");
        }
    }

    @Override
    @Transactional
    public ShoppingList deleteList(Long shopId) throws ValidationException, AuthorizationException, ConflictException {
        LOGGER.trace("deleteList({})", shopId);

        // Authentication (check the correct user)
        ApplicationUser applicationUser = authService.getUserFromToken();

        // Authorization (check if the user can work with this object)
        ShoppingList check = shoppingListRepository.findByIdAndSharedFlatIs(shopId, applicationUser.getSharedFlat());
        if (check == null) {
            throw new AuthorizationException("Authorization failed", List.of("User has no access to this shopping list!"));
        }

        // Attempt to find and delete the shopping list
        Optional<ShoppingList> deletedListOptional = shoppingListRepository.findById(shopId);
        if (deletedListOptional.isPresent()) {
            ShoppingList deletedList = deletedListOptional.get();
            if (!deletedList.getItems().isEmpty()) {
                this.deleteShoppingItems(itemMapper.shoppingItemEntityListToShoppingItemDtoList(deletedList.getItems())
                    .stream()
                    .map(ShoppingItemDto::itemId)
                    .collect(Collectors.toList()));
            }
            shoppingListRepository.deleteById(shopId);
            return deletedList;
        } else {
            throw new ValidationException("Validation failed", List.of("Shopping list not found"));
        }
    }


    @Override
    public List<ShoppingList> getShoppingLists(String name) throws AuthorizationException {
        LOGGER.trace("getShoppingLists()");
        ApplicationUser applicationUser = authService.getUserFromToken();

        List<ShoppingList> ret = shoppingListRepository.findAllByNameContainingIgnoreCaseAndSharedFlatIs(name != null ? name : "", applicationUser.getSharedFlat());
        for (ShoppingList shoppingList : ret) {
            shoppingList.setItems(this.getItemsByShoppingListId(shoppingList.getId(), new ShoppingItemSearchDto(null, null, null)));
        }
        return ret;
    }

    @Override
    @Transactional
    public List<DigitalStorageItem> transferToServer(List<ShoppingItemDto> items) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("transferToServer({})", items);
        ApplicationUser applicationUser = authService.getUserFromToken();
        List<DigitalStorage> storage = digitalStorageRepository.findBySharedFlatIs(applicationUser.getSharedFlat());
        List<DigitalStorageItem> ret = new ArrayList<>();
        for (ShoppingItemDto itemDto : items) {
            DigitalStorageItem item;
            if (itemDto.alwaysInStock() != null && itemDto.alwaysInStock()) {
                item = shoppingListMapper.shoppingItemDtoToAisEntity(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()), storage.get(0));
            } else {
                item = shoppingListMapper.shoppingItemDtoToItemEntity(itemDto, ingredientMapper.dtoListToEntityList(itemDto.ingredients()), storage.get(0));
            }
            itemService.create(itemMapper.entityToDto(item));
            ret.add(item);
        }
        this.deleteShoppingItems(items.stream()
            .map(ShoppingItemDto::itemId)
            .collect(Collectors.toList()));
        return ret;
    }

    @Override
    @Transactional
    public ShoppingItem updateShoppingItem(ShoppingItemDto itemDto)
        throws ConflictException, ValidationException, AuthorizationException {
        LOGGER.trace("update({})", itemDto);

        shoppingItemRepository.findByItemId(itemDto.itemId()).orElseThrow(() -> new NotFoundException("Given Id does not exist in the database!"));

        List<ShoppingList> shoppingLists = this.getShoppingLists("");
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        shoppingItemValidator.validateForUpdate(itemDto, shoppingLists, digitalStorageList, unitList);

        List<ItemLabel> labels = null;
        if (itemDto.labels() != null) {
            labels = findLabelsAndCreateMissing(itemDto.labels());
        }
        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        ShoppingItem item = itemMapper.shoppingItemDtoToShoppingItemEntity(itemDto, labels);
        item.getItemCache().setIngredientList(ingredientList);
        item.setLabels(labels);
        return shoppingItemRepository.save(item);
    }

    @Override
    @Transactional
    public List<ShoppingItem> deleteShoppingItems(List<Long> itemIds) throws AuthorizationException, ConflictException {
        LOGGER.trace("deleteItems({})", itemIds);
        List<ShoppingItem> toDelete = shoppingItemRepository.findAllById(itemIds);
        if (toDelete.size() != itemIds.size()) {
            throw new NotFoundException("The given shopping items do not exist in the persistent data");
        }
        ApplicationUser user = authService.getUserFromToken();
        for (ShoppingItem shoppingItem : toDelete) {
            if (!user.getSharedFlat().equals(shoppingItem.getShoppingList().getSharedFlat())) {
                throw new AuthorizationException("Authorization error", List.of("User has no access to shopping item: " + shoppingItem.getItemCache().getProductName()));
            }
        }
        ShoppingList shoppingList = toDelete.get(0).getShoppingList();
        for (ShoppingItem shoppingItem : toDelete) {
            shoppingList.getItems().remove(shoppingItem);
            if (!shoppingItem.getLabels().isEmpty()) {
                shoppingItem.setLabels(new ArrayList<>());
                shoppingItemRepository.save(shoppingItem);
            }
        }
        shoppingListRepository.save(shoppingList);
        shoppingItemRepository.deleteAllById(itemIds);
        return toDelete;
    }

    private List<ItemLabel> findLabelsAndCreateMissing(List<ItemLabelDto> labels) throws ValidationException, ConflictException {
        LOGGER.trace("findLabelsAndCreateMissing({})", labels);
        if (labels == null) {
            return List.of();
        }
        for (ItemLabelDto itemLabelDto : labels) {
            itemLabelValidator.validate(itemLabelDto);
        }

        List<String> values = labels.stream()
            .map(ItemLabelDto::labelValue)
            .toList();
        List<String> colours = labels.stream()
            .map(ItemLabelDto::labelColour)
            .toList();

        List<ItemLabel> ret = new ArrayList<>();
        if (!values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                ItemLabel found = labelService.findByValueAndColour(values.get(i), colours.get(i));
                if (found != null) {
                    ret.add(found);
                }
            }
        }

        List<ItemLabelDto> missingLabels = labels.stream()
            .filter(labelDto ->
                ret.stream()
                    .noneMatch(label ->
                        (label.getLabelValue().equals(labelDto.labelValue())
                            && label.getLabelColour().equals(labelDto.labelColour()))
                    )
            ).toList();

        if (!missingLabels.isEmpty()) {
            List<ItemLabel> createdLabels = labelService.createAll(missingLabels);
            ret.addAll(createdLabels);
        }
        return ret;
    }
}
