package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import com.deepl.api.DeepLException;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/cooking")
public class CookingEndPoint {
    private final CookingService cookingService;
    private final RecipeMapper recipeMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CookingEndPoint(CookingService cookingService, RecipeMapper recipeMapper) {
        this.cookingService = cookingService;
        this.recipeMapper = recipeMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    public List<RecipeSuggestionDto> getRecipeSuggestion(String type)
        throws ValidationException, ConflictException, AuthorizationException, AuthenticationException, DeepLException, InterruptedException {
        return cookingService.getRecipeSuggestion(type);
    }

    @Secured("ROLE_USER")
    @GetMapping("/cookbook")
    public List<RecipeSuggestionDto> getCookbook()
        throws ValidationException, AuthorizationException, AuthenticationException {
        return cookingService.getCookbook();
    }

    @Secured("ROLE_USER")
    @PostMapping("/cookbook")
    public RecipeSuggestionDto createCookbookRecipe(@RequestBody RecipeSuggestionDto recipe)
        throws ConflictException, ValidationException, AuthorizationException, AuthenticationException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.createCookbookRecipe(recipe));
    }

    @Secured("ROLE_USER")
    @GetMapping("/cookbook/{id}")
    public RecipeSuggestionDto getCookbookRecipe(@PathVariable Long id) {
        return cookingService.getCookbookRecipe(id);
    }

    @Secured("ROLE_USER")
    @PutMapping("/cookbook/{id}")
    public RecipeSuggestionDto updateCookbookRecipe(@PathVariable Long id, @RequestBody RecipeSuggestionDto recipe)
        throws ConflictException, ValidationException, AuthorizationException, AuthenticationException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.updateCookbookRecipe(recipe.withId(id)));
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/cookbook/{id}")
    public RecipeSuggestionDto deleteCookbookRecipe(@PathVariable Long id)
        throws AuthorizationException, AuthenticationException {
        return recipeMapper.entityToRecipeSuggestionDto(cookingService.deleteCookbookRecipe(id));
    }

    @Secured("ROLE_USER")
    @GetMapping("/cookbook/missing/{id}")
    public RecipeSuggestionDto getMissingIngredients(@PathVariable Long id)
        throws AuthorizationException, ValidationException, ConflictException {
        return cookingService.getMissingIngredients(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("/detail/{id}")
    public RecipeDetailDto getRecipeDetail(@PathVariable Long id) {
        return cookingService.getRecipeDetails(id);
    }

    @Secured("ROLE_USER")
    @PutMapping("/cook")
    public RecipeSuggestionDto cookRecipe(@RequestBody RecipeSuggestionDto recipeToCook)
        throws ValidationException, ConflictException, AuthorizationException, AuthenticationException {
        return cookingService.cookRecipe(recipeToCook);
    }

    @Secured("ROLE_USER")
    @PostMapping("/shopping")
    public RecipeSuggestionDto addToShoppingList(@RequestBody RecipeSuggestionDto recipeToCook)
        throws ValidationException, AuthenticationException, ConflictException, AuthorizationException {
        return cookingService.addToShoppingList(recipeToCook);
    }

    @Secured("ROLE_USER")
    @PutMapping("/unmatchitems")
    public RecipeIngredientDto unMatchIngredient(@RequestBody String ingredientName) {
        return cookingService.unMatchIngredient(ingredientName);
    }


}
