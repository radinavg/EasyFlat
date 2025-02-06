package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookbookDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import com.deepl.api.DeepLException;

import java.util.List;
import java.util.Optional;

public interface CookingService {

    /**
     * Get a list of recipe suggestions based on the provided store ID and type.
     *
     * @param type The type of the recipe (e.g., breakfast, main dish ...).
     * @return A list of recipe suggestions.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getRecipeSuggestion(String type)
        throws ValidationException, ConflictException, AuthorizationException, AuthenticationException, DeepLException, InterruptedException;

    /**
     * Get the details of a specific recipe based on the provided recipe ID.
     *
     * @param recipeId The ID of the recipe.
     * @return The details of the recipe.
     */
    RecipeDetailDto getRecipeDetails(Long recipeId);

    /**
     *  * Get the list of all existing cookbooks.
     *
     * @return the cookbooks
     */
    List<Cookbook> findAllCookbooks() throws AuthorizationException, AuthenticationException;

    /**
     * Get a list of recipes from the cookbook.
     *
     * @return A list of recipes.
     * @throws ValidationException If there is a validation error.
     */
    List<RecipeSuggestionDto> getCookbook() throws ValidationException, AuthorizationException, AuthenticationException;

    /**
     * Create a new recipe in the cookbook.
     *
     * @param recipe The recipe details to be added to the cookbook.
     * @return The created recipe.
     * @throws ConflictException If there is a conflict with existing data.
     */
    RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe)
        throws ConflictException, ValidationException, AuthorizationException, AuthenticationException;

    /**
     * Get a specific recipe from the cookbook based on its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return An Optional containing the recipe, if found.
     */
    RecipeSuggestionDto getCookbookRecipe(Long id);

    /**
     * Update an existing recipe in the cookbook.
     *
     * @param recipe The updated recipe details.
     * @return The updated recipe.
     * @throws ConflictException If there is a conflict with existing data.
     */
    RecipeSuggestion updateCookbookRecipe(RecipeSuggestionDto recipe)
        throws ConflictException, ValidationException, AuthorizationException, AuthenticationException;

    /**
     * Delete a recipe from the cookbook based on its ID.
     *
     * @param id The ID of the recipe to be deleted.
     * @return The deleted recipe.
     */
    RecipeSuggestion deleteCookbookRecipe(Long id) throws AuthorizationException, AuthenticationException;

    /**
     * Get a list of missing ingredients for a specific recipe from the cookbook.
     *
     * @param id The ID of the recipe to check for missing ingredients.
     * @return The missing ingredients for the recipe.
     */
    RecipeSuggestionDto getMissingIngredients(Long id) throws AuthorizationException, ValidationException, ConflictException;

    /**
     * Cook the given recipe by subtracting the used ingredients from the digital storage.
     *
     * @param recipeToCook the recipe that needs to be cooked
     * @return the cooked recipe
     */
    RecipeSuggestionDto cookRecipe(RecipeSuggestionDto recipeToCook)
        throws ValidationException, ConflictException, AuthorizationException, AuthenticationException;

    /**
     * Add the missing ingredients from a recipe to the shopping list.
     *
     * @param recipeToCook the recipe with the missing ingredients
     * @return the recipe with the missing ingredients
     */
    RecipeSuggestionDto addToShoppingList(RecipeSuggestionDto recipeToCook)
        throws AuthenticationException, ValidationException, ConflictException, AuthorizationException;


    RecipeIngredientDto unMatchIngredient(String ingredientName);
}
