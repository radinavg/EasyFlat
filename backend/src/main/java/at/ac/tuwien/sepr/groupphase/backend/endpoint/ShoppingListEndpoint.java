package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/shopping")
public class ShoppingListEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingListService shoppingService;
    private final ItemMapper itemMapper;
    private final ShoppingListMapper shoppingListMapper;


    public ShoppingListEndpoint(ShoppingListService shoppingService, ItemMapper mapper, ShoppingListMapper shoppingListMapper, IngredientMapper ingredientsMapper, LabelMapper labelMapper) {
        this.shoppingService = shoppingService;
        this.itemMapper = mapper;
        this.shoppingListMapper = shoppingListMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingItemDto createShoppingItem(@RequestBody ShoppingItemDto itemDto)
        throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.info("createShoppingItem({})", itemDto);
        ShoppingItem item = shoppingService.createShoppingItem(itemDto);
        return itemMapper.shoppingItemEntityToShoppingItemDto(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    public ShoppingItemDto updateShoppingItem(@PathVariable long id, @RequestBody ShoppingItemDto itemDto)
        throws ValidationException, ConflictException, AuthenticationException, AuthorizationException {
        LOGGER.info("update({},{})", id, itemDto);
        ShoppingItem item = shoppingService.updateShoppingItem(itemDto.withId(id));
        return itemMapper.shoppingItemEntityToShoppingItemDto(item, shoppingListMapper.entityToDto(item.getShoppingList()));
    }

    @Secured("ROLE_USER")
    @GetMapping("{itemId}")
    public Optional<ShoppingItemDto> getShoppingItemById(@PathVariable Long itemId) throws AuthorizationException {
        LOGGER.info("getById({})", itemId);
        Optional<ShoppingItem> item = shoppingService.getShoppingItemById(itemId);
        return item.flatMap(currentItem -> Optional.ofNullable(itemMapper.shoppingItemEntityToShoppingItemDto(currentItem,
            shoppingListMapper.entityToDto(currentItem.getShoppingList()))));
    }


    @Secured("ROLE_USER")
    @GetMapping("/list/{name}")
    public Optional<ShoppingListDto> getShoppingListByName(@PathVariable String name) {
        LOGGER.info("getShoppingListById({})", name);
        Optional<ShoppingList> ret = shoppingService.getShoppingListByName(name);
        return ret.flatMap(shoppingList -> Optional.ofNullable(shoppingListMapper.entityToDto(shoppingList)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/listId/{id}")
    public Optional<ShoppingListDto> getShoppingListById(@PathVariable Long id) {
        LOGGER.info("getShoppingListById({})", id);
        Optional<ShoppingList> ret = shoppingService.getShoppingListById(id);
        return ret.flatMap(shoppingList -> Optional.ofNullable(shoppingListMapper.entityToDto(shoppingList)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/list-items/{id}")
    public List<ShoppingItemDto> getItemsByShoppingListId(@PathVariable Long id, ShoppingItemSearchDto itemSearchDto) throws AuthorizationException {
        LOGGER.info("getItemsById({})", id);
        List<ShoppingItem> items = shoppingService.getItemsByShoppingListId(id, itemSearchDto);
        List<ShoppingItemDto> ret = new ArrayList<>();
        for (ShoppingItem item : items) {
            ret.add(itemMapper.shoppingItemEntityToShoppingItemDto(item, shoppingListMapper.entityToDto(item.getShoppingList())));
        }
        return ret;
    }

    @Secured("ROLE_USER")
    @PostMapping("/list-create")
    public ShoppingListDto createList(@RequestBody ShoppingListDto listDto) throws ValidationException, ConflictException {
        LOGGER.info("createList({})", listDto);
        ShoppingList shoppingList = shoppingService.createList(listDto);
        return shoppingListMapper.entityToDto(shoppingList);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{itemId}")
    public ShoppingItemDto deleteItem(@PathVariable Long itemId) throws AuthorizationException, ConflictException {
        LOGGER.info("deleteItem({})", itemId);
        ShoppingItem deletedItem = shoppingService.deleteItem(itemId);
        return itemMapper.shoppingItemEntityToShoppingItemDto(deletedItem, shoppingListMapper.entityToDto(deletedItem.getShoppingList()));
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/delete/{shopId}")
    public ShoppingListDto deleteList(@PathVariable Long shopId) throws ValidationException, AuthorizationException, ConflictException {
        LOGGER.info("deleteList({})", shopId);
        ShoppingList deletedList = shoppingService.deleteList(shopId);
        return shoppingListMapper.entityToDto(deletedList);
    }

    @Secured("ROLE_USER")
    @GetMapping("/lists")
    public List<ShoppingListDto> getShoppingLists(@RequestParam(name = "searchParams", required = false) String searchParams) throws AuthorizationException {
        LOGGER.info("getShoppingLists({})", searchParams);
        List<ShoppingList> lists = shoppingService.getShoppingLists(searchParams);
        return shoppingListMapper.entityListToDtoList(lists);
    }

    @Secured("ROLE_USER")
    @PostMapping("/storage")
    public List<ItemDto> transferToStorage(@RequestBody List<ShoppingItemDto> items) throws AuthorizationException, ValidationException, ConflictException {
        LOGGER.info("transferToStorage({})", items);
        List<DigitalStorageItem> res = this.shoppingService.transferToServer(items);
        List<ItemDto> toRet = new ArrayList<>();
        for (DigitalStorageItem digitalStorageItem : res) {
            toRet.add(itemMapper.entityToDto(digitalStorageItem));
        }
        return toRet;
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/delete")
    public List<ShoppingItemDto> deleteShoppingItems(@RequestParam(name = "itemIds") String itemIdsString) throws AuthorizationException, ConflictException {
        List<Long> itemIds = Arrays.stream(itemIdsString.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());
        List<ShoppingItem> ret = shoppingService.deleteShoppingItems(itemIds);
        return itemMapper.shoppingItemEntityListToShoppingItemDtoList(ret);
    }
}
