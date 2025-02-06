package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class RecipeMapper {

    @Mapping(target = "extendedIngredients", expression = "java( ingredients )")
    @Mapping(target = "missingIngredients", source = "recipeSuggestionDto.missedIngredients")
    @Mapping(target = "version", ignore = true)
    public abstract RecipeSuggestion dtoToEntity(RecipeSuggestionDto recipeSuggestionDto,
                                                 @Context List<RecipeIngredient> ingredients);


    public abstract RecipeSuggestionDto entityToRecipeSuggestionDto(RecipeSuggestion recipeSuggestion);
}
