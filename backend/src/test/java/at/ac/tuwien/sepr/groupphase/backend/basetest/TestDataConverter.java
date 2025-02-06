package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;

public class TestDataConverter {

    public static ItemDto convertToAlwaysInStockItemDto(ItemDto itemDto, Long minimumQuantity) {
        return ItemDtoBuilder.builder()
            .itemId(itemDto.itemId())
            .ean(itemDto.ean())
            .generalName(itemDto.generalName())
            .productName(itemDto.productName())
            .alwaysInStock(true)
            .minimumQuantity(minimumQuantity)
            .quantityCurrent(itemDto.quantityCurrent())
            .quantityTotal(itemDto.quantityTotal())
            .unit(itemDto.unit())
            .description(itemDto.description())
            .brand(itemDto.brand())
            .digitalStorage(itemDto.digitalStorage())
            .build();
    }

    public static ItemDto convertToInStockItemDto(ItemDto itemDto) {
        return ItemDtoBuilder.builder()
            .itemId(itemDto.itemId())
            .ean(itemDto.ean())
            .generalName(itemDto.generalName())
            .productName(itemDto.productName())
            .alwaysInStock(false)
            .quantityCurrent(itemDto.quantityCurrent())
            .quantityTotal(itemDto.quantityTotal())
            .unit(itemDto.unit())
            .description(itemDto.description())
            .brand(itemDto.brand())
            .digitalStorage(itemDto.digitalStorage())
            .build();
    }

    public static ItemDto updateUnit(ItemDto itemDto, UnitDto unit) {
        return ItemDtoBuilder.builder()
            .itemId(itemDto.itemId())
            .ean(itemDto.ean())
            .generalName(itemDto.generalName())
            .productName(itemDto.productName())
            .alwaysInStock(itemDto.alwaysInStock())
            .quantityCurrent(itemDto.quantityCurrent())
            .quantityTotal(itemDto.quantityTotal())
            .unit(unit)
            .description(itemDto.description())
            .brand(itemDto.brand())
            .digitalStorage(itemDto.digitalStorage())
            .build();
    }

    public static ItemDto updateProductName(ItemDto itemDto, String productName) {
        return ItemDtoBuilder.builder()
            .itemId(itemDto.itemId())
            .ean(itemDto.ean())
            .generalName(itemDto.generalName())
            .productName(productName)
            .alwaysInStock(itemDto.alwaysInStock())
            .quantityCurrent(itemDto.quantityCurrent())
            .quantityTotal(itemDto.quantityTotal())
            .unit(itemDto.unit())
            .description(itemDto.description())
            .brand(itemDto.brand())
            .digitalStorage(itemDto.digitalStorage())
            .build();
    }
}
