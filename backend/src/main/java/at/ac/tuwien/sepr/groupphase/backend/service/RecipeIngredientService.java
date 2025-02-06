package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;

import java.util.List;

public interface RecipeIngredientService {

    List<RecipeIngredient> createAll(List<RecipeIngredientDto> ingredients);

    List<RecipeIngredient> findByName(List<String> names);

    RecipeIngredientDto unMatchIngredient(String ingredientName);
}
