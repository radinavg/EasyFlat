package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
public record OpenFoodFactsProductDto(
    @JsonProperty("generic_name")
    String genericName,
    @JsonProperty("generic_name_de")
    String genericNameDe,
    @JsonProperty("generic_name_en")
    String genericNameEn,
    @JsonProperty("product_name")
    String productName,
    @JsonProperty("product_name_de")
    String productNameDe,
    @JsonProperty("product_name_en")
    String productNameEn,
    String brands,
    @JsonProperty("product_quantity")
    Long productQuantity,
    @JsonProperty("ecoscore_data")
    OpenFoodFactsEcoscoreDataDto ecoscoreData,
    @JsonProperty("category_properties")
    OpenFoodFactsCategoryPropertiesDto categoryProperties,
    List<OpenFoodFactsIngredientDto> ingredients,
    @JsonProperty("stores")
    String boughtAt

) {
}
