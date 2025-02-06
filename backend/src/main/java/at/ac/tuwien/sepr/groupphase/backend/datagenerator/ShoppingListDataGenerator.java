package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"test", "generateData", "unitTest"})
@Component("ShoppingListPresentationDataGenerator")
@DependsOn({"CleanDatabase"})
public class ShoppingListDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListDataGenerator(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostConstruct
    public void generateShoppingLists() {
        LOGGER.debug("generating {} ShoppingLists", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId((long) i + 1);

            // ShoppingList Default
            ShoppingList def = new ShoppingList();
            def.setName("Shopping List (Default)");
            def.setSharedFlat(sharedFlat);
            shoppingListRepository.save(def);

            // ShoppingList 1: Tech
            ShoppingList techList = new ShoppingList();
            techList.setName("Tech");
            techList.setSharedFlat(sharedFlat);
            shoppingListRepository.save(techList);

            // ShoppingList 2: Home Improvements
            ShoppingList homeImprovementsList = new ShoppingList();
            homeImprovementsList.setName("Home Improvements");
            homeImprovementsList.setSharedFlat(sharedFlat);
            shoppingListRepository.save(homeImprovementsList);

            // ShoppingList 3: Foodstuff
            ShoppingList foodstuffList = new ShoppingList();
            foodstuffList.setName("Foodstuff");
            foodstuffList.setSharedFlat(sharedFlat);
            shoppingListRepository.save(foodstuffList);
        }
    }
}
