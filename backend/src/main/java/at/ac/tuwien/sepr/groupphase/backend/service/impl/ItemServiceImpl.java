package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFieldSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.AlternativeNameService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ItemValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final IngredientService ingredientService;
    private final ItemMapper itemMapper;
    private final ItemValidator itemValidator;
    private final AuthService authService;
    private final Authorization authorization;
    private final UnitService unitService;

    private final AlternativeNameService alternativeNameService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           DigitalStorageService digitalStorageService,
                           IngredientService ingredientService,
                           ItemMapper itemMapper,
                           ItemValidator itemValidator,
                           AuthService authService,
                           Authorization authorization,
                           UnitService unitService,
                           AlternativeNameService alternativeNameService) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.authService = authService;
        this.authorization = authorization;
        this.unitService = unitService;
        this.alternativeNameService = alternativeNameService;
    }

    @Override
    public DigitalStorageItem findById(Long id) throws AuthorizationException {
        LOGGER.trace("findById({})", id);

        DigitalStorageItem persistedDigitalStorageItem = itemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Digital storage item not found"));

        List<Long> allowedUsers = persistedDigitalStorageItem.getDigitalStorage().getSharedFlat().getUsers().stream().map(ApplicationUser::getId).toList();

        authorization.authorizeUser(
            allowedUsers,
            "User does not have access to this item"
        );

        return persistedDigitalStorageItem;
    }

    @Override
    public List<DigitalStorageItem> findAll(int limit) {
        LOGGER.trace("findAll()");

        ApplicationUser applicationUser = authService.getUserFromToken();

        Long digitalStorageId = applicationUser.getSharedFlat().getDigitalStorage().getStorageId();

        if (limit > 0) {
            return itemRepository.findAllByDigitalStorage_StorageId(digitalStorageId).stream().limit(limit).collect(Collectors.toList());
        } else {
            return itemRepository.findAllByDigitalStorage_StorageId(digitalStorageId);
        }
    }

    @Override
    public List<DigitalStorageItem> findByFields(ItemFieldSearchDto itemFieldSearchDto) {
        LOGGER.trace("findByFields({})", itemFieldSearchDto);

        return itemRepository.findAllByItemCache_GeneralNameContainingIgnoreCaseOrItemCache_BrandContainingIgnoreCaseOrBoughtAtContainingIgnoreCase(
            itemFieldSearchDto.generalName(),
            itemFieldSearchDto.brand(),
            itemFieldSearchDto.boughtAt()
        );
    }

    @Override
    public List<DigitalStorageItem> getItemWithGeneralName(String generalName) {
        LOGGER.trace("getItemWithGeneralName({})", generalName);

        ApplicationUser applicationUser = authService.getUserFromToken();

        Long digitalStorageId = applicationUser.getSharedFlat().getDigitalStorage().getStorageId();

        return itemRepository.findAllByDigitalStorage_StorageIdIsAndItemCache_GeneralNameContainsIgnoreCase(digitalStorageId, generalName);
    }

    @Override
    @Transactional
    public DigitalStorageItem create(ItemDto itemDto) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("create({})", itemDto);

        ApplicationUser applicationUser = authService.getUserFromToken();

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        List<DigitalStorageItem> digitalStorageItemList = itemRepository.findAllByDigitalStorage_StorageId(
            applicationUser.getSharedFlat().getDigitalStorage().getStorageId()
        );
        itemValidator.validateForCreate(itemDto, digitalStorageList, unitList, digitalStorageItemList);

        if (!(Objects.equals(applicationUser.getSharedFlat().getDigitalStorage().getStorageId(), itemDto.digitalStorage().storageId()))) {
            throw new AuthorizationException("The given digital storage does not belong to the user", List.of());
        }

        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        List<AlternativeName> alternativeNames = new LinkedList<>();
        DigitalStorageItem digitalStorageItem;
        if (itemDto.alwaysInStock()) {
            digitalStorageItem = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, alternativeNames);
        } else {
            digitalStorageItem = itemMapper.dtoToEntity(itemDto, ingredientList, alternativeNames);
        }
        DigitalStorageItem createdDigitalStorageItem = itemRepository.save(digitalStorageItem);
        createdDigitalStorageItem.setIngredientList(ingredientList);
        return createdDigitalStorageItem;
    }

    @Override
    @Transactional
    public DigitalStorageItem update(ItemDto itemDto) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.trace("update({})", itemDto);

        ApplicationUser applicationUser = authService.getUserFromToken();

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        List<Unit> unitList = unitService.findAll();
        List<DigitalStorageItem> digitalStorageItemList = itemRepository.findAllByDigitalStorage_StorageId(
            applicationUser.getSharedFlat().getDigitalStorage().getStorageId()
        );
        itemValidator.validateForUpdate(itemDto, digitalStorageList, unitList, digitalStorageItemList);

        if (!(Objects.equals(applicationUser.getSharedFlat().getDigitalStorage().getStorageId(), itemDto.digitalStorage().storageId()))) {
            throw new AuthorizationException("The given digital storage does not belong to the user", List.of());
        }

        List<Ingredient> ingredientList = ingredientService.findIngredientsAndCreateMissing(itemDto.ingredients());

        DigitalStorageItem digitalStorageItem;

        if (itemDto.alwaysInStock()) {
            digitalStorageItem = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, itemMapper.alternativeNamesDtoToEntityList(itemDto.alternativeNames()));
        } else {
            digitalStorageItem = itemMapper.dtoToEntity(itemDto, ingredientList, itemMapper.alternativeNamesDtoToEntityList(itemDto.alternativeNames()));
        }

        DigitalStorageItem presistedDigitalStorageItem = this.findById(itemDto.itemId());

        digitalStorageItem.getItemCache().setId(presistedDigitalStorageItem.getItemCache().getId());


        digitalStorageItem.getItemCache().setAlternativeNames(getAlternativeNamesForUpdate(itemDto));

        // necessary because JPA cannot convert an Entity to another Entity
        if (digitalStorageItem.alwaysInStock() != presistedDigitalStorageItem.alwaysInStock()) {
            this.delete(itemDto.itemId());
        }

        if (itemDto.alwaysInStock() && itemDto.quantityCurrent() < itemDto.minimumQuantity()) {
            digitalStorageService.addItemToShopping(itemDto);
        }

        DigitalStorageItem updatedDigitalStorageItem = itemRepository.save(digitalStorageItem);
        updatedDigitalStorageItem.setIngredientList(ingredientList);

        if (!itemDto.alwaysInStock() && itemDto.quantityCurrent() <= 0) {
            delete(itemDto.itemId());
        }

        return updatedDigitalStorageItem;
    }

    @Override
    @Transactional
    public void delete(Long id) throws AuthorizationException {
        LOGGER.trace("delete({})", id);

        findById(id);

        itemRepository.deleteById(id);
    }

    @Override
    public List<DigitalStorageItem> findByName(String name, String unitName) {
        ApplicationUser user = authService.getUserFromToken();
        List<DigitalStorageItem> items = itemRepository.findAllByDigitalStorage_StorageIdAndItemCache_ProductNameStartingWithIgnoreCase(user.getSharedFlat().getDigitalStorage().getStorageId(), name);
        List<DigitalStorageItem> filtered = filterItemsByUnits(items, unitName);
        return filterItemsByUnits(items, unitName);
    }

    private List<AlternativeName> getAlternativeNamesForUpdate(ItemDto itemDto) {
        List<AlternativeName> alternativeNames = new LinkedList<>();
        if (itemDto.alternativeNames() != null) {
            if (!itemDto.alternativeNames().isEmpty()) {
                for (AlternativeNameDto alternativeNameDto : itemDto.alternativeNames()) {
                    if (alternativeNameDto.id() != null) {
                        try {
                            AlternativeName toAdd = alternativeNameService.findById(alternativeNameDto.id());
                            alternativeNames.add(toAdd);
                        } catch (NotFoundException e) {
                            AlternativeName toAdd = new AlternativeName();
                            toAdd.setName(alternativeNameDto.name());
                            toAdd.setShareFlatId(authService.getUserFromToken().getSharedFlat().getId());
                            alternativeNames.add(toAdd);
                        }
                    } else {
                        AlternativeName toAdd = new AlternativeName();
                        toAdd.setName(alternativeNameDto.name());
                        toAdd.setShareFlatId(authService.getUserFromToken().getSharedFlat().getId());
                        alternativeNames.add(toAdd);
                    }
                }

            }
        }
        return alternativeNames;
    }

    private List<DigitalStorageItem> filterItemsByUnits(List<DigitalStorageItem> items, String unitName) {
        Unit unit = unitService.findByName(unitName);

        if (unit == null) {
            return Collections.emptyList();
        }

        return items.stream()
            .filter(item -> {
                Unit itemUnit = item.getItemCache().getUnit();
                return itemUnit != null
                    && unitService.areUnitsComparable(itemUnit, unit);
            })
            .collect(Collectors.toList());
    }
}
