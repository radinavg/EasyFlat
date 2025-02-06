package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ItemFromOpenFoodFactsApiMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final IngredientService ingredientService;
    private final UnitService unitService;
    private final IngredientMapper ingredientMapper;
    private final UnitMapper unitMapper;

    public ItemFromOpenFoodFactsApiMapper(IngredientService ingredientService,
                                          UnitService unitService,
                                          IngredientMapper ingredientMapper,
                                          UnitMapper unitMapper) {
        this.ingredientService = ingredientService;
        this.unitService = unitService;
        this.ingredientMapper = ingredientMapper;
        this.unitMapper = unitMapper;
    }

    public OpenFoodFactsItemDto mapFromJsonNode(OpenFoodFactsResponseDto openFoodFactsResponseDto) throws ConflictException {
        LOGGER.trace("mapFromJsonNode({})", openFoodFactsResponseDto);

        if (openFoodFactsResponseDto.status()) {
            if (openFoodFactsResponseDto.product() == null) {
                return null;
            } else {

                final String ean = openFoodFactsResponseDto.eanCode();
                final String generalName = Optional.ofNullable(
                    openFoodFactsResponseDto.product().genericName()
                ).orElse(
                    Optional.ofNullable(
                        openFoodFactsResponseDto.product().genericNameEn()
                    ).orElse(
                        openFoodFactsResponseDto.product().genericNameDe()
                    )
                );
                final String productName = Optional.ofNullable(
                    openFoodFactsResponseDto.product().productName()
                ).orElse(
                    Optional.ofNullable(
                        openFoodFactsResponseDto.product().productNameEn()
                    ).orElse(
                        openFoodFactsResponseDto.product().productNameEn()
                    )
                );

                final String brand = openFoodFactsResponseDto.product().brands();
                final Long totalQuantity = openFoodFactsResponseDto.product().productQuantity();
                final String unit = getString(openFoodFactsResponseDto);
                String description = null;
                if (openFoodFactsResponseDto.product().categoryProperties() != null) {
                    description = openFoodFactsResponseDto.product().categoryProperties().description();
                }
                final String boughtAt = openFoodFactsResponseDto.product().boughtAt();
                List<OpenFoodFactsIngredientDto> ingredientList = openFoodFactsResponseDto.product().ingredients();

                List<IngredientDto> ingredients = null;

                if (ingredientList != null && !ingredientList.isEmpty()) {
                    // Create a pattern to match non-letter characters - because every ingredient should only consist of letters
                    Pattern nonLetterPattern = Pattern.compile("[^\\p{L}]+");

                    List<IngredientDto> ingredientDtoList = ingredientList.stream()
                        .map(OpenFoodFactsIngredientDto::text) // Extract text
                        .map(text -> nonLetterPattern.matcher(text).replaceAll("")) // Remove non-letter characters
                        .map(cleanedText -> IngredientDtoBuilder.builder()
                            .name(cleanedText)
                            .build())
                        .collect(Collectors.toList());

                    ingredients = ingredientMapper.entityListToDtoList(ingredientService.findIngredientsAndCreateMissing(ingredientDtoList));
                }

                UnitDto unitDto = null;
                try {
                    unitDto = unitMapper.entityToUnitDto(unitService.findByName(unit));
                } catch (NotFoundException e) {
                    LOGGER.info("Unit {} not found in database", unit);
                }

                return new OpenFoodFactsItemDto(
                    ean,
                    !Objects.equals(generalName, "") ? generalName : productName,
                    productName,
                    brand,
                    totalQuantity,
                    unitDto,
                    description,
                    boughtAt,
                    ingredients
                );
            }
        } else {
            throw new NotFoundException("EAN not found in API");
        }
    }

    private String getString(OpenFoodFactsResponseDto openFoodFactsResponseDto) {
        String unit = null;
        if (openFoodFactsResponseDto.product().ecoscoreData() != null
            && openFoodFactsResponseDto.product().ecoscoreData().adjustments() != null
            && openFoodFactsResponseDto.product().ecoscoreData().adjustments().packaging() != null
            && openFoodFactsResponseDto.product().ecoscoreData().adjustments().packaging().packagings() != null
            && !openFoodFactsResponseDto.product().ecoscoreData().adjustments().packaging().packagings().isEmpty()) {

            unit = openFoodFactsResponseDto.product().ecoscoreData().adjustments().packaging().packagings().get(0).unit();
        }
        return unit;
    }
}
