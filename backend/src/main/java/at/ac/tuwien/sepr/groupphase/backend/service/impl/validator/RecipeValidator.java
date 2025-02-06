package at.ac.tuwien.sepr.groupphase.backend.service.impl.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import jakarta.validation.ConstraintViolation;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RecipeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Validator validator;
    private final CookbookRepository cookbookRepository;

    private final RecipeMapper recipeMapper;
    private final AuthService authService;
    private final RecipeIngredientMapper recipeIngredientMapper;

    public RecipeValidator(Validator validator, CookbookRepository cookbookRepository, RecipeMapper recipeMapper, AuthService authService, RecipeIngredientMapper recipeIngredientMapper) {
        this.validator = validator;
        this.cookbookRepository = cookbookRepository;
        this.recipeMapper = recipeMapper;
        this.authService = authService;
        this.recipeIngredientMapper = recipeIngredientMapper;
    }

    public void validateForCreate(RecipeSuggestionDto recipeSuggestionDto) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreate({})", recipeSuggestionDto);

        checkForDataValidation(recipeSuggestionDto);
        checkCookbookForCreate(recipeSuggestionDto);
        validateIngredients(recipeSuggestionDto.extendedIngredients());

        Set<ConstraintViolation<RecipeSuggestionDto>> validationViolations = validator.validate(recipeSuggestionDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }

    }

    public void validateForUpdate(RecipeSuggestionDto recipeSuggestionDto) throws ValidationException {
        LOGGER.trace("checkValidationForUpdate({})", recipeSuggestionDto);

        validateIngredients(recipeSuggestionDto.extendedIngredients());

        Set<ConstraintViolation<RecipeSuggestionDto>> validationViolations = validator.validate(recipeSuggestionDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    public void validateForCook(RecipeSuggestionDto recipeSuggestionDto) throws ValidationException {
        LOGGER.trace("checkValidationForCook({})", recipeSuggestionDto);

        Set<ConstraintViolation<RecipeSuggestionDto>> validationViolations = validator.validate(recipeSuggestionDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkForDataValidation(RecipeSuggestionDto recipeSuggestionDto) throws ValidationException {
        Set<ConstraintViolation<RecipeSuggestionDto>> validationViolations = validator.validate(recipeSuggestionDto);
        if (!validationViolations.isEmpty()) {
            throw new ValidationException("The data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
        }
    }

    private void checkCookbookForCreate(RecipeSuggestionDto recipeSuggestionDto) throws ConflictException {
        List<String> errors = new ArrayList<>();
        ApplicationUser user = authService.getUserFromToken();
        List<Cookbook> cookbookList = cookbookRepository.findBySharedFlatIs(user.getSharedFlat());
        Cookbook cookbook = cookbookList.get(0);
        List<RecipeSuggestion> recipeSuggestions = cookbook.getRecipes();
        for (RecipeSuggestion recipeSuggestion : recipeSuggestions) {
            if (recipeSuggestion.equals(recipeMapper.dtoToEntity(recipeSuggestionDto, recipeIngredientMapper.dtoListToEntityList(recipeSuggestionDto.extendedIngredients())))) {
                errors.add("Recipe exists");
            }
        }
        if (!errors.isEmpty()) {
            throw new ConflictException("Conflict with other data", errors);
        }
    }

    public void validateIngredients(List<RecipeIngredientDto> ingredients) throws ValidationException {
        LOGGER.trace("validateIngredients({})", ingredients);

        for (RecipeIngredientDto ingredient : ingredients) {
            Set<ConstraintViolation<RecipeIngredientDto>> validationViolations = validator.validate(ingredient);
            if (!validationViolations.isEmpty()) {
                throw new ValidationException("Ingredient data is not valid: ", validationViolations.stream().map(ConstraintViolation::getMessage).toList());
            }
        }
    }


}
