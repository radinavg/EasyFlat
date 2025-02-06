package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeIngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeIngredientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class RecipeIngredientServiceImpl implements RecipeIngredientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RecipeIngredientRepository ingredientRepository;
    private final RecipeIngredientMapper ingredientMapper;

    public RecipeIngredientServiceImpl(RecipeIngredientRepository ingredientRepository,
                                       RecipeIngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }


    @Override
    public List<RecipeIngredient> createAll(
        List<RecipeIngredientDto> ingredients) {
        LOGGER.trace("createAll({})", ingredients);
        List<RecipeIngredient> ingredientList = ingredientMapper.dtoListToEntityList(ingredients);
        return ingredientRepository.saveAll(ingredientList);
    }

    @Override
    public List<RecipeIngredient> findByName(List<String> names) {
        LOGGER.trace("findByNames({})", names);
        return ingredientRepository.findAllByNameIsIn(names);
    }

    @Override
    public RecipeIngredientDto unMatchIngredient(String ingredientName) {
        List<RecipeIngredient> ingredients = ingredientRepository.findAllByNameIsIn(List.of(ingredientName));
        for (RecipeIngredient ingredient : ingredients) {
            if (ingredient.getRealName() != null) {
                ingredient.setName(ingredient.getRealName());
                ingredient.setRealName(null);
            }
        }
        ingredientRepository.saveAll(ingredients);
        if (!ingredients.isEmpty()) {
            return ingredientMapper.entityToDto(ingredients.get(0));
        } else {
            return null;
        }
    }
}
