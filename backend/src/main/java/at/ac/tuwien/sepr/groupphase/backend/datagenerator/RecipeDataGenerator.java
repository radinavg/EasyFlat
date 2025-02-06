package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeIngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Profile({"generateData", "test"})
@Component("RecipeDataGenerator")
@DependsOn({"CleanDatabase", "CookbookDataGenerator", "UnitDataGenerator"})
public class RecipeDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RecipeSuggestionRepository recipeSuggestionRepository;
    private final UnitRepository unitRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;


    public RecipeDataGenerator(RecipeSuggestionRepository recipeSuggestionRepository,
                               UnitRepository unitRepository,
                               RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeSuggestionRepository = recipeSuggestionRepository;
        this.unitRepository = unitRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    @PostConstruct
    public void generateItems() {
        LOGGER.debug("generating Items");
        Cookbook cookbook = new Cookbook();
        cookbook.setId(1L);

        RecipeSuggestion recipe1 = new RecipeSuggestion();
        recipe1.setTitle("Spaghetti Bolognese");
        recipe1.setSummary("Classic Italian pasta dish with savory meat sauce. Begin by browning 500g of ground beef in a pan. "
            + "Add 400ml of tomato sauce and let it simmer, allowing the flavors to meld. Meanwhile, cook spaghetti until al dente. "
            + "Serve the rich meat sauce over the perfectly cooked spaghetti.");
        recipe1.setServings(4);
        recipe1.setReadyInMinutes(30);
        recipe1.setCookbook(cookbook);

        RecipeIngredient ingredient1v1 = new RecipeIngredient();
        ingredient1v1.setName("Ground Beef");
        ingredient1v1.setAmount(500);
        Unit g = unitRepository.findByName("g").orElseThrow();
        ingredient1v1.setUnit(g.getName());
        ingredient1v1.setUnitEnum(g);

        RecipeIngredient ingredient1v2 = new RecipeIngredient();
        ingredient1v2.setName("Tomato Sauce");
        ingredient1v2.setAmount(400);
        Unit ml = unitRepository.findByName("ml").orElseThrow();
        ingredient1v2.setUnit(ml.getName());
        ingredient1v2.setUnitEnum(ml);

        List<RecipeIngredient> ingredients1 = List.of(ingredient1v1, ingredient1v2);
        recipeIngredientRepository.saveAll(ingredients1);
        recipe1.setExtendedIngredients(ingredients1);
        recipeSuggestionRepository.save(recipe1);

        RecipeSuggestion recipe2 = new RecipeSuggestion();
        recipe2.setTitle("Easy Pasta Carbonara");
        recipe2.setSummary("A quick and creamy pasta dish with bacon and Parmesan cheese. Cook 200g of spaghetti until al dente. "
            + "In a separate bowl, whisk together 2 eggs and 50g of Parmesan cheese. In a pan, cook 100g of bacon until crispy. "
            + "Toss the cooked spaghetti with the egg and cheese mixture, adding the bacon. Serve immediately for a delightful carbonara.");
        recipe2.setServings(2);
        recipe2.setReadyInMinutes(20);
        recipe2.setCookbook(cookbook);

        RecipeIngredient ingredient2v1 = new RecipeIngredient();
        ingredient2v1.setName("Spaghetti");
        ingredient2v1.setAmount(200);
        ingredient2v1.setUnit(g.getName());
        ingredient2v1.setUnitEnum(g);

        RecipeIngredient ingredient2v2 = new RecipeIngredient();
        ingredient2v2.setName("Bacon");
        ingredient2v2.setAmount(100);
        ingredient2v2.setUnit(g.getName());
        ingredient2v2.setUnitEnum(g);

        RecipeIngredient ingredient2v3 = new RecipeIngredient();
        ingredient2v3.setName("Eggs");
        ingredient2v3.setAmount(2);
        Unit pcs = unitRepository.findByName("pcs").orElseThrow();
        ingredient2v3.setUnit(pcs.getName());
        ingredient2v3.setUnitEnum(pcs);

        RecipeIngredient ingredient2v4 = new RecipeIngredient();
        ingredient2v4.setName("Parmesan Cheese");
        ingredient2v4.setAmount(50);
        ingredient2v4.setUnit(g.getName());
        ingredient2v4.setUnitEnum(g);

        List<RecipeIngredient> ingredients2 = List.of(ingredient2v1, ingredient2v2, ingredient2v3, ingredient2v4);
        recipeIngredientRepository.saveAll(ingredients2);
        recipe2.setExtendedIngredients(ingredients2);
        recipeSuggestionRepository.save(recipe2);

        RecipeSuggestion recipe3 = new RecipeSuggestion();
        recipe3.setTitle("Chicken and Vegetable Soup");
        recipe3.setSummary("A nourishing and easy-to-make soup with chicken and assorted vegetables. Start by cooking 300g of chicken breast "
            + "until fully cooked. Add 2 diced carrots, 2 diced potatoes, and 1 chopped onion to the pot. Let the ingredients simmer in "
            + "chicken broth for 30 minutes. The result is a hearty and flavorful chicken and vegetable soup.");
        recipe3.setServings(4);
        recipe3.setReadyInMinutes(30);
        recipe3.setCookbook(cookbook);

        RecipeIngredient ingredient3v1 = new RecipeIngredient();
        ingredient3v1.setName("Chicken Breast");
        ingredient3v1.setAmount(300);
        ingredient3v1.setUnit(g.getName());
        ingredient3v1.setUnitEnum(g);

        RecipeIngredient ingredient3v2 = new RecipeIngredient();
        ingredient3v2.setName("Carrots");
        ingredient3v2.setAmount(2);
        ingredient3v2.setUnit(pcs.getName());
        ingredient3v2.setUnitEnum(pcs);

        RecipeIngredient ingredient3v3 = new RecipeIngredient();
        ingredient3v3.setName("Potatoes");
        ingredient3v3.setAmount(2);
        ingredient3v3.setUnit(pcs.getName());
        ingredient3v3.setUnitEnum(pcs);

        RecipeIngredient ingredient3v4 = new RecipeIngredient();
        ingredient3v4.setName("Onion");
        ingredient3v4.setAmount(1);
        ingredient3v4.setUnit(pcs.getName());
        ingredient3v4.setUnitEnum(pcs);

        List<RecipeIngredient> ingredients3 = List.of(ingredient3v1, ingredient3v2, ingredient3v3, ingredient3v4);
        recipeIngredientRepository.saveAll(ingredients3);
        recipe3.setExtendedIngredients(ingredients3);
        recipeSuggestionRepository.save(recipe3);


        RecipeSuggestion recipe4 = new RecipeSuggestion();
        recipe4.setTitle("Grandma's Breakfast Pancakes");
        recipe4.setSummary("Delicious and fluffy pancakes made with love, just like grandma used to make. In a bowl, mix 200g of flour, "
            + "250ml of milk, 2 eggs, and 25g of sugar. Heat a griddle or pan and pour the pancake batter. Cook until bubbles form on "
            + "the surface, then flip and cook until golden brown. Serve these nostalgic pancakes with your favorite toppings.");
        recipe4.setServings(2);
        recipe4.setReadyInMinutes(20);
        recipe4.setCookbook(cookbook);

        RecipeIngredient ingredient4v1 = new RecipeIngredient();
        ingredient4v1.setName("Flour");
        ingredient4v1.setAmount(200);
        ingredient4v1.setUnit(g.getName());
        ingredient4v1.setUnitEnum(g);

        RecipeIngredient ingredient4v2 = new RecipeIngredient();
        ingredient4v2.setName("Milk");
        ingredient4v2.setAmount(250);
        ingredient4v2.setUnit(ml.getName());
        ingredient4v2.setUnitEnum(ml);

        RecipeIngredient ingredient4v3 = new RecipeIngredient();
        ingredient4v3.setName("Eggs");
        ingredient4v3.setAmount(2);
        ingredient4v3.setUnit(pcs.getName());
        ingredient4v3.setUnitEnum(pcs);

        RecipeIngredient ingredient4v4 = new RecipeIngredient();
        ingredient4v4.setName("Sugar");
        ingredient4v4.setAmount(25);
        ingredient4v4.setUnit(g.getName());
        ingredient4v4.setUnitEnum(g);


        List<RecipeIngredient> ingredients4 = List.of(ingredient4v1, ingredient4v2, ingredient4v3, ingredient4v4);
        recipeIngredientRepository.saveAll(ingredients4);
        recipe4.setExtendedIngredients(ingredients4);
        recipeSuggestionRepository.save(recipe4);


        RecipeSuggestion recipe5 = new RecipeSuggestion();
        recipe5.setTitle("Homemade Vegetable Soup");
        recipe5.setSummary("Hearty and nutritious vegetable soup made from scratch. Begin by sautéing 150g of onions and 2 cloves of garlic "
            + "in a pot. Add 250g of carrots, 300g of potatoes, and 1 liter of vegetable broth. Let the soup simmer for 40 minutes, "
            + "resulting in a comforting and flavorful homemade vegetable soup.");
        recipe5.setServings(6);
        recipe5.setReadyInMinutes(40);
        recipe5.setCookbook(cookbook);

        RecipeIngredient ingredient5v1 = new RecipeIngredient();
        ingredient5v1.setName("Carrots");
        ingredient5v1.setAmount(250);
        ingredient5v1.setUnit(g.getName());
        ingredient5v1.setUnitEnum(g);

        RecipeIngredient ingredient5v2 = new RecipeIngredient();
        ingredient5v2.setName("Potatoes");
        ingredient5v2.setAmount(300);
        ingredient5v2.setUnit(g.getName());
        ingredient5v2.setUnitEnum(g);

        RecipeIngredient ingredient5v3 = new RecipeIngredient();
        ingredient5v3.setName("Onions");
        ingredient5v3.setAmount(150);
        ingredient5v3.setUnit(g.getName());
        ingredient5v3.setUnitEnum(g);

        RecipeIngredient ingredient5v4 = new RecipeIngredient();
        ingredient5v4.setName("Garlic");
        ingredient5v4.setAmount(2);
        ingredient5v4.setUnit(pcs.getName());
        ingredient5v4.setUnitEnum(pcs);

        RecipeIngredient ingredient5v5 = new RecipeIngredient();
        ingredient5v5.setName("Vegetable Broth");
        ingredient5v5.setAmount(1);
        Unit l = unitRepository.findByName("l").orElseThrow();
        ingredient5v5.setUnit(l.getName());
        ingredient5v5.setUnitEnum(l);

        List<RecipeIngredient> ingredients5 = List.of(ingredient5v1, ingredient5v2, ingredient5v3, ingredient5v4, ingredient5v5);
        recipeIngredientRepository.saveAll(ingredients5);
        recipe5.setExtendedIngredients(ingredients5);
        recipeSuggestionRepository.save(recipe5);

    }
}
