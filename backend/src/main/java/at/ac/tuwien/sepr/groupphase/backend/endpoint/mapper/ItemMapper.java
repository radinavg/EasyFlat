package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {IngredientMapper.class, DigitalStorageMapper.class, UnitMapper.class})
public abstract class ItemMapper {

    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "itemCache.ingredientList", expression = "java( ingredientList )")
    @Mapping(target = "itemCache.alternativeNames", expression = "java( alternativeNames )")
    public abstract DigitalStorageItem dtoToEntity(ItemDto itemDto,
                                                   @Context List<Ingredient> ingredientList,
                                                   @Context List<AlternativeName> alternativeNames);

    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "ingredientList", expression = "java( ingredientList )")
    @Mapping(target = "itemCache.alternativeNames", expression = "java( alternativeNames )")
    public abstract AlwaysInStockDigitalStorageItem dtoToAlwaysInStock(ItemDto itemDto,
                                                                       @Context List<Ingredient> ingredientList,
                                                                       @Context List<AlternativeName> alternativeNames);


    @Mapping(target = "ean", source = "itemCache.ean")
    @Mapping(target = "generalName", source = "itemCache.generalName")
    @Mapping(target = "productName", source = "itemCache.productName")
    @Mapping(target = "brand", source = "itemCache.brand")
    @Mapping(target = "quantityTotal", source = "itemCache.quantityTotal")
    @Mapping(target = "unit", source = "itemCache.unit")
    @Mapping(target = "description", source = "itemCache.description")
    @Mapping(target = "ingredients", source = "itemCache.ingredientList")
    @Mapping(target = "alternativeNames", source = "itemCache.alternativeNames")
    @Mapping(target = "alwaysInStock", expression = "java( digitalStorageItem.alwaysInStock() )")
    @Mapping(target = "minimumQuantity", expression = "java( digitalStorageItem.getMinimumQuantity() )")
    public abstract ItemDto entityToDto(DigitalStorageItem digitalStorageItem);

    @Mapping(target = "labels", expression = "java( labels )")
    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "itemCache.ingredientList", source = "ingredients")
    public abstract ShoppingItem shoppingItemDtoToShoppingItemEntity(ShoppingItemDto itemDto,
                                                                     @Context List<ItemLabel> labels);

    @Mapping(target = "ean", source = "itemCache.ean")
    @Mapping(target = "generalName", source = "itemCache.generalName")
    @Mapping(target = "productName", source = "itemCache.productName")
    @Mapping(target = "brand", source = "itemCache.brand")
    @Mapping(target = "quantityTotal", source = "itemCache.quantityTotal")
    @Mapping(target = "unit", source = "itemCache.unit")
    @Mapping(target = "description", source = "itemCache.description")
    @Mapping(target = "ingredients", source = "itemCache.ingredientList")
    public abstract ShoppingItemDto shoppingItemEntityToShoppingItemDto(ShoppingItem item,
                                                                        @Context ShoppingListDto shoppingList);

    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "itemCache.ean", source = "ean")
    @Mapping(target = "itemCache.generalName", source = "generalName")
    @Mapping(target = "itemCache.productName", source = "productName")
    @Mapping(target = "itemCache.brand", source = "brand")
    @Mapping(target = "itemCache.quantityTotal", source = "quantityTotal")
    @Mapping(target = "itemCache.unit", source = "unit")
    @Mapping(target = "itemCache.description", source = "description")
    @Mapping(target = "itemCache.ingredientList", expression = "java( ingredients )")
    @Mapping(target = "shoppingList", expression = "java( shoppingList )")
    public abstract ShoppingItem itemDtoToShoppingItemEntity(ItemDto itemDto,
                                                             @Context List<Ingredient> ingredients,
                                                             @Context ShoppingList shoppingList);

    public abstract List<ShoppingItemDto> shoppingItemEntityListToShoppingItemDtoList(List<ShoppingItem> digitalStorageItems);

    public abstract List<ItemDto> entityListToItemDtoList(List<DigitalStorageItem> digitalStorageItems);

    @Mapping(target = "ean", source = "eanCode")
    public abstract ItemDto openFoodFactItemDtoToItemDto(OpenFoodFactsItemDto openFoodFactsItemDto);

    public abstract List<AlternativeNameDto> alternativeNamesEntityToDtoList(List<AlternativeName> alternativeNames);

    public abstract AlternativeNameDto alternativeNameEntityToDto(AlternativeName alternativeNames);

    public abstract List<AlternativeName> alternativeNamesDtoToEntityList(List<AlternativeNameDto> alternativeNameDtos);

    public abstract AlternativeName alternativeNameDtoToEntity(AlternativeNameDto alternativeNameDtos);
}
