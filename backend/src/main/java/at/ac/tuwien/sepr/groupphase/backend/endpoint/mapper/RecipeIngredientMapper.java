package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class RecipeIngredientMapper {


    public abstract List<RecipeIngredient> dtoListToEntityList(List<RecipeIngredientDto> recipeIngredientDtos);

    public abstract RecipeIngredient dtoToEntity(RecipeIngredientDto recipeIngredientDto);

    public abstract RecipeIngredientDto entityToDto(RecipeIngredient recipeIngredient);

    public abstract List<RecipeIngredientDto> entityListToDtoList(List<RecipeIngredient> recipeIngredients);

}
