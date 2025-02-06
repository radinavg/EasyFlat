package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.List;


public interface IngredientService {

    /**
     * Search for all Ingredients which has a given id.
     *
     * @param ids a list of Ids
     * @return a list of all ingredients with a given id
     */
    List<Ingredient> findAllByIds(List<Long> ids);

    /**
     * Creates new Ingredient in the database.
     *
     * @param ingredients a list of IngredientDto which will be created
     * @return a list of persisted ingredients
     * @throws ConflictException if a given ingredient has a id
     */
    List<Ingredient> createAll(List<IngredientDto> ingredients) throws ConflictException;

    /**
     * Search for all Ingredients which has a given name.
     *
     * @param names a list of names
     * @return a list of all ingredients with a given name
     */
    List<Ingredient> findByTitle(List<String> names);

    /**
     * Check if ingredient already exists and create it if it does not exist.
     *
     * @param ingredientDtoList Takes a list of IngredientDtos that are either given or created using the IngredientBuilder
     * @return a list of all the ingredients given
     * @throws ConflictException if a given ingredient already has an id
     */

    List<Ingredient> findIngredientsAndCreateMissing(List<IngredientDto> ingredientDtoList) throws ConflictException;
}
