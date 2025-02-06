package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@RecordBuilder
public record ShoppingItemDto(
    Long itemId,

    String ean,
    @NotBlank(message = "The product category cannot be blank")
    @Size(max = 30, message = "The product category cannot have more than 30 characters")
    String generalName,
    @NotBlank(message = "The product name cannot be blank")
    @Size(max = 40, message = "The product name cannot have more than 40 characters")
    String productName,
    @Size(max = 30, message = "The brand name cannot have more than 30 characters")
    String brand,
    @NotNull(message = "The quantity cannot be empty and needs to be a number")
    @Min(value = 0, message = "The quantity must be positive")
    @Max(value = 5000, message = "The quantity cannot be greater than 5000")
    Double quantityCurrent,
    Double quantityTotal,
    @NotNull(message = "The unit cannot be null")
    UnitDto unit,
    @Size(max = 200, message = "The product name cannot have more than 40 characters")
    String description,
    @Min(value = 0)
    Long priceInCent,
    Boolean alwaysInStock,
    
    Double minimumQuantity,
    String boughtAt,
    List<IngredientDto> ingredients,
    @Size(max = 3, message = "The labels count can be at most 3")
    List<ItemLabelDto> labels,
    ShoppingListDto shoppingList
) {

    public ShoppingItemDto withId(long newId) {
        return new ShoppingItemDto(
            newId,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            boughtAt,
            ingredients,
            labels,
            shoppingList
        );
    }

    public ShoppingItemDto withAlwaysInStock(long newId, boolean alwaysInStock) {
        return new ShoppingItemDto(
            newId,
            ean,
            generalName,
            productName,
            brand,
            quantityCurrent,
            quantityTotal,
            unit,
            description,
            priceInCent,
            alwaysInStock,
            minimumQuantity,
            boughtAt,
            ingredients,
            labels,
            shoppingList
        );
    }

    /**
     * This method converts the current quantity to a string and then uses regex to
     * check if the number does not exceed the maximum amount of decimal places.
     *
     * @return true - if it is valid; false - if it is not valid
     */
    @AssertTrue(message = "Quantity cannot have more than 2 decimal places")
    private boolean isQuantityCurrentValidDecimalPlaces() {

        if (this.quantityCurrent == null || this.quantityCurrent > 5000) {
            return true;
        }

        int maximumDecimalPlaces = 2;

        String valueString = this.quantityCurrent.toString();

        String regex = "^-?\\d+(\\.\\d{1," + maximumDecimalPlaces + "})?$";
        // fully qualified name necessary due to conflict with Jakarta Pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);

        return pattern.matcher(valueString).matches();
    }
}
