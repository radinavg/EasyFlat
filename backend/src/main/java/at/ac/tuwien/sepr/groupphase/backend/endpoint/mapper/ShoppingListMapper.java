package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UnitMapper.class, IngredientMapper.class, DigitalStorageMapper.class})
public abstract class ShoppingListMapper {

    @Mapping(target = "itemsCount", expression = "java( shoppingList.getItemsCount() )")
    public abstract ShoppingListDto entityToDto(ShoppingList shoppingList);

    public abstract ShoppingList dtoToEntity(ShoppingListDto shoppingListDto);

    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    public abstract List<ShoppingListDto> entityListToDtoList(List<ShoppingList> shoppingList);


    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "ingredientList", source = "ingredients")
    @Mapping(target = "digitalStorage", expression = "java( digitalStorage )")
    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    public abstract DigitalStorageItem shoppingItemDtoToItemEntity(ShoppingItemDto shoppingItemDto,
                                                                   @Context List<Ingredient> ingredients,
                                                                   @Context DigitalStorage digitalStorage);

    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "ingredientList", source = "ingredients")
    @Mapping(target = "digitalStorage", expression = "java( digitalStorage )")
    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    public abstract AlwaysInStockDigitalStorageItem shoppingItemDtoToAisEntity(ShoppingItemDto shoppingItem,
                                                                               @Context List<Ingredient> ingredients,
                                                                               @Context DigitalStorage digitalStorage);
}
