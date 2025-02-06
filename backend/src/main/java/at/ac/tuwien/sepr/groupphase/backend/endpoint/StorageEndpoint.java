package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/storage")
public class StorageEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DigitalStorageService digitalStorageService;
    private final DigitalStorageMapper digitalStorageMapper;
    private final ItemMapper itemMapper;
    private final ShoppingListMapper shoppingListMapper;

    public StorageEndpoint(DigitalStorageService digitalStorageService, DigitalStorageMapper digitalStorageMapper, ItemMapper itemMapper, ShoppingListMapper shoppingListMapper) {
        this.digitalStorageService = digitalStorageService;
        this.digitalStorageMapper = digitalStorageMapper;
        this.itemMapper = itemMapper;
        this.shoppingListMapper = shoppingListMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    public List<DigitalStorageDto> findAll(DigitalStorageSearchDto digitalStorageDto) throws AuthorizationException {
        LOGGER.info("findAll({})", digitalStorageDto);

        return digitalStorageMapper.entityListToDtoList(
            digitalStorageService.findAll(digitalStorageDto)
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemListDto> getStorageItems(ItemSearchDto itemSearchDto) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.info("getStorageItems({})", itemSearchDto);
        return digitalStorageService.searchItems(itemSearchDto);
    }

    @Secured("ROLE_USER")
    @PostMapping("/shop")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemDto addItemToShopping(@RequestBody ItemDto itemDto) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.info("addItemToShopping({})", itemDto);
        ShoppingItem item = digitalStorageService.addItemToShopping(itemDto);
        return itemMapper.shoppingItemEntityToShoppingItemDto(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }
}
