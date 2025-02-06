package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test", "unitTest"})
@Component("IngredientsDataGenerator")
@DependsOn("CleanDatabase")
public class IngredientsDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final IngredientRepository ingredientRepository;

    public IngredientsDataGenerator(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @PostConstruct
    public void generateIngredients() {
        LOGGER.debug("generating {} Ingredients", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {

            Ingredient ingredient1 = new Ingredient();
            ingredient1.setTitle("Ingredient " + (i + 1));

            LOGGER.debug("saving item {}", ingredient1);
            ingredientRepository.save(ingredient1);
        }
    }
}
