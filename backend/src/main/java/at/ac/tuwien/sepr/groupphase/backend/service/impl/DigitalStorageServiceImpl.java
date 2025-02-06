package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.DigitalStorageValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service

public class DigitalStorageServiceImpl implements DigitalStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DigitalStorageRepository digitalStorageRepository;
    private final DigitalStorageMapper digitalStorageMapper;
    private final DigitalStorageValidator digitalStorageValidator;
    private final ItemMapper itemMapper;
    private final ShoppingItemRepository shoppingItemRepository;
    private final IngredientMapper ingredientMapper;
    private final Authorization authorization;
    private final AuthService authService;
    private final UnitService unitService;
    private final ShoppingListRepository shoppingListRepository;

    public DigitalStorageServiceImpl(DigitalStorageRepository digitalStorageRepository,
                                     DigitalStorageMapper digitalStorageMapper,
                                     DigitalStorageValidator digitalStorageValidator,
                                     ItemMapper itemMapper,
                                     ShoppingItemRepository shoppingItemRepository,
                                     IngredientMapper ingredientMapper,
                                     AuthService authService,
                                     Authorization authorization,
                                     ShoppingListRepository shoppingListRepository,
                                     UnitService unitService) {
        this.digitalStorageRepository = digitalStorageRepository;
        this.digitalStorageMapper = digitalStorageMapper;
        this.digitalStorageValidator = digitalStorageValidator;
        this.itemMapper = itemMapper;
        this.shoppingItemRepository = shoppingItemRepository;
        this.ingredientMapper = ingredientMapper;
        this.authService = authService;
        this.authorization = authorization;
        this.shoppingListRepository = shoppingListRepository;
        this.unitService = unitService;
    }

    @Override
    public DigitalStorage findById(Long id) throws AuthorizationException {
        LOGGER.trace("findById({})", id);

        if (id == null) {
            throw new NotFoundException("No storage ID given!");
        }

        Optional<DigitalStorage> digitalStorage = digitalStorageRepository.findById(id);

        if (digitalStorage.isEmpty()) {
            throw new NotFoundException("The given item ID could not be found in the database!");
        }

        List<Long> allowedUsers = digitalStorage.get().getSharedFlat().getUsers().stream().map(ApplicationUser::getId).toList();
        authorization.authorizeUser(
            allowedUsers
        );

        return digitalStorage.get();
    }

    @Override
    public List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto) throws AuthorizationException {
        LOGGER.trace("findAll({})", digitalStorageSearchDto);

        ApplicationUser applicationUser = authService.getUserFromToken();

        return digitalStorageRepository.findByTitleContainingAndSharedFlatIs(
            (digitalStorageSearchDto != null && digitalStorageSearchDto.title() != null)
                ? digitalStorageSearchDto.title()
                : "",
            applicationUser.getSharedFlat()
        );
    }

    @Override
    public List<ItemListDto> searchItems(ItemSearchDto searchItem) throws ValidationException {
        LOGGER.trace("searchItems({})", searchItem);

        ApplicationUser applicationUser = authService.getUserFromToken();

        digitalStorageValidator.validateForSearchItems(searchItem);

        Long storageId = applicationUser.getSharedFlat().getDigitalStorage().getStorageId();

        Class alwaysInStock;
        if (searchItem.alwaysInStock() == null || !searchItem.alwaysInStock()) {
            alwaysInStock = DigitalStorageItem.class;
        } else {
            alwaysInStock = AlwaysInStockDigitalStorageItem.class;
        }

        List<DigitalStorageItem> allDigitalStorageItems = digitalStorageRepository.searchItems(
            storageId,
            (searchItem.productName() != null) ? searchItem.productName() : null,
            (searchItem.fillLevel() != null) ? searchItem.fillLevel() : null,
            alwaysInStock
        );

        List<ItemListDto> groupedItems = prepareListItemsForStorage(allDigitalStorageItems);
        List<ItemListDto> orderedGroups = groupedItems.stream()
            .sorted((g1, g2) ->
                sortItems(searchItem, g1, g2)
            ).collect(Collectors.toList());
        if (searchItem.desc() == null || searchItem.desc()) {
            Collections.reverse(orderedGroups);
        }
        return orderedGroups;
    }


    /**
     * The create method is only used for creating storages used in digital storage tests.
     */
    @Transactional
    @Override
    public DigitalStorage create(DigitalStorageDto storageDto) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("create({})", storageDto);

        digitalStorageValidator.validateForCreate(storageDto);

        DigitalStorage storage = digitalStorageMapper.dtoToEntity(storageDto);

        return digitalStorageRepository.save(storage);
    }

    @Transactional
    @Override
    public ShoppingItem addItemToShopping(ItemDto itemDto) {
        LOGGER.trace("addItemToShopping({})", itemDto);

        ApplicationUser applicationUser = authService.getUserFromToken();

        ShoppingList shoppingList = shoppingListRepository.findByNameAndSharedFlatIs("Shopping List (Default)", applicationUser.getSharedFlat());
        if (shoppingList == null) {
            throw new NotFoundException("Shopping List not found!");
        }
        ShoppingItem shoppingItem = itemMapper.itemDtoToShoppingItemEntity(itemDto,
            ingredientMapper.dtoListToEntityList(itemDto.ingredients()),
            shoppingList
        );
        return shoppingItemRepository.save(shoppingItem);
    }

    private List<ItemListDto> prepareListItemsForStorage(List<DigitalStorageItem> allDigitalStorageItems) {
        Map<String, Double[]> items = new HashMap<>();
        Map<String, Unit> itemUnits = new HashMap<>();
        for (DigitalStorageItem digitalStorageItem : allDigitalStorageItems) {
            itemUnits.computeIfAbsent(digitalStorageItem.getItemCache().getGeneralName(), k -> digitalStorageItem.getItemCache().getUnit());

            double currentQ = 0;
            double totalQ = 0;
            if (items.get(digitalStorageItem.getItemCache().getGeneralName()) != null) {
                currentQ = items.get(digitalStorageItem.getItemCache().getGeneralName())[0];
                totalQ = items.get(digitalStorageItem.getItemCache().getGeneralName())[2];
            }

            Double updatedQuantityCurrent = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), itemUnits.get(digitalStorageItem.getItemCache().getGeneralName()), digitalStorageItem.getQuantityCurrent());
            Double updatedQuantityTotal = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), itemUnits.get(digitalStorageItem.getItemCache().getGeneralName()), digitalStorageItem.getItemCache().getQuantityTotal());


            Double[] quantityStorageId = new Double[3];
            quantityStorageId[0] = currentQ + updatedQuantityCurrent;
            quantityStorageId[1] = digitalStorageItem.getDigitalStorage().getStorageId().doubleValue();
            quantityStorageId[2] = totalQ + updatedQuantityTotal;
            items.put(digitalStorageItem.getItemCache().getGeneralName(), quantityStorageId);
        }
        List<ItemListDto> toRet = new LinkedList<>();
        for (Map.Entry<String, Double[]> item : items.entrySet()) {
            toRet.add(new ItemListDto(item.getKey(), item.getValue()[0], item.getValue()[2], item.getValue()[1].longValue(), UnitDtoBuilder.builder().name(itemUnits.get(item.getKey()).getName()).build()));
        }
        return toRet;
    }

    private static int sortItems(ItemSearchDto searchItem, ItemListDto g1, ItemListDto g2) {
        if (searchItem.orderType() == null) {
            return 0;
        }
        if (searchItem.orderType() == ItemOrderType.QUANTITY_CURRENT) {
            int compareUnitNames = g1.unit().name().toLowerCase().compareTo(g2.unit().name().toLowerCase());
            if (compareUnitNames != 0) {
                return compareUnitNames;
            }
            return g1.quantityCurrent().compareTo(g2.quantityCurrent());
        } else if (searchItem.orderType() == ItemOrderType.GENERAL_NAME) {
            return g1.generalName().toLowerCase().compareTo(g2.generalName().toLowerCase());
        } else {
            return 0;
        }
    }
}
