package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemCache;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

@Profile({"generateData", "test"})
@Component("ShoppingItemDataGenerator")
@DependsOn({"CleanDatabase", "UnitDataGenerator",
    "ShoppingListPresentationDataGenerator", "labelRepository", "ingredientRepository"})
public class ShoppingItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingItemRepository shoppingItemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final LabelRepository itemLabelRepository;
    private final UnitRepository unitRepository;
    private final IngredientRepository ingredientRepository;

    private Unit gram = new Unit();
    private Unit kg = new Unit();
    private Unit pcs = new Unit();
    private Unit liter = new Unit();

    private ShoppingList def;
    private ShoppingList tech;
    private ShoppingList homeImprovements;
    private ShoppingList foodstuff;

    private ItemLabel label1;
    private ItemLabel label2;
    private ItemLabel label3;
    private ItemLabel label4;
    private ItemLabel label5;
    private ItemLabel label6;
    private ItemLabel label7;
    private ItemLabel label8;
    private ItemLabel label9;
    private ItemLabel label10;


    public ShoppingItemDataGenerator(ShoppingItemRepository shoppingItemRepository, ShoppingListRepository shoppingListRepository,
                                     LabelRepository itemLabelRepository, UnitRepository unitRepository, IngredientRepository ingredientRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.itemLabelRepository = itemLabelRepository;
        this.unitRepository = unitRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @PostConstruct
    public void generateShoppingItems() {
        LOGGER.info("generateShoppingItems()");
        def = shoppingListRepository.findById(1L).orElseThrow();
        tech = shoppingListRepository.findById(2L).orElseThrow();
        homeImprovements = shoppingListRepository.findById(3L).orElseThrow();
        foodstuff = shoppingListRepository.findById(4L).orElseThrow();

        gram = unitRepository.findByName("g").orElseThrow();
        kg = unitRepository.findByName("kg").orElseThrow();
        pcs = unitRepository.findByName("pcs").orElseThrow();
        liter = unitRepository.findByName("l").orElseThrow();

        label1 = itemLabelRepository.findById(1L).orElse(null);
        label2 = itemLabelRepository.findById(2L).orElse(null);
        label3 = itemLabelRepository.findById(3L).orElse(null);
        label4 = itemLabelRepository.findById(4L).orElse(null);
        label5 = itemLabelRepository.findById(5L).orElse(null);
        label6 = itemLabelRepository.findById(6L).orElse(null);
        label7 = itemLabelRepository.findById(7L).orElse(null);
        label8 = itemLabelRepository.findById(8L).orElse(null);
        label9 = itemLabelRepository.findById(9L).orElse(null);
        label10 = itemLabelRepository.findById(10L).orElse(null);


        // ShoppingItem 1
        ShoppingItem item1 = new ShoppingItem();
        item1.setQuantityCurrent(3.0);
        item1.setAlwaysInStock(true);
        item1.setMinimumQuantity(2.0);
        item1.setPriceInCent(350L);
        item1.setBoughtAt("Local Bakery");
        ItemCache itemCache1 = createBakeryItemCache();
        item1.setItemCache(itemCache1);
        item1.setShoppingList(foodstuff);
        shoppingItemRepository.save(item1);

        // ShoppingItem 2
        ShoppingItem item2 = new ShoppingItem();
        item2.setQuantityCurrent(1.0);
        item2.setAlwaysInStock(false);
        item2.setMinimumQuantity(0.5);
        item2.setBoughtAt("Fresh Fish Market");
        ItemCache itemCache2 = createSeafoodItemCache();
        item2.setItemCache(itemCache2);
        item2.setLabels(Arrays.asList(label2));
        item2.setShoppingList(foodstuff);
        shoppingItemRepository.save(item2);

        // ShoppingItem 3
        ShoppingItem item3 = new ShoppingItem();
        item3.setQuantityCurrent(750.0);
        item3.setAlwaysInStock(true);
        item3.setMinimumQuantity(500.0);
        item3.setPriceInCent(1200L);
        item3.setBoughtAt("Organic Farm Stand");
        ItemCache itemCache3 = createOrganicProduceItemCache();
        item3.setItemCache(itemCache3);
        item3.setLabels(Arrays.asList(label7, label2));
        item3.setShoppingList(foodstuff);
        shoppingItemRepository.save(item3);

        // ShoppingItem 4
        ShoppingItem item4 = new ShoppingItem();
        item4.setQuantityCurrent(1.0);
        item4.setAlwaysInStock(true);
        item4.setMinimumQuantity(0.5);
        item4.setPriceInCent(500L);
        item4.setBoughtAt("Tech Store");
        ItemCache itemCache4 = createTechItemCache();
        item4.setItemCache(itemCache4);
        item4.setLabels(Arrays.asList(label3));
        item4.setShoppingList(tech);
        shoppingItemRepository.save(item4);

        // ShoppingItem 5
        ShoppingItem item5 = new ShoppingItem();
        item5.setQuantityCurrent(6.0);
        item5.setAlwaysInStock(true);
        item5.setMinimumQuantity(5.0);
        item5.setBoughtAt("Gourmet Cheese Shop");
        ItemCache itemCache5 = createCheeseItemCache();
        item5.setItemCache(itemCache5);
        item5.setShoppingList(foodstuff);
        shoppingItemRepository.save(item5);

        // ShoppingItem 6
        ShoppingItem item6 = new ShoppingItem();
        item6.setQuantityCurrent(500.0);
        item6.setAlwaysInStock(true);
        item6.setMinimumQuantity(250.0);
        item6.setBoughtAt("Local Butcher");
        ItemCache itemCache6 = createMeatItemCache();
        item6.setItemCache(itemCache6);
        item6.setLabels(Arrays.asList(label1, label3));
        item6.setShoppingList(foodstuff);
        shoppingItemRepository.save(item6);

        // ShoppingItem 7
        ShoppingItem item7 = new ShoppingItem();
        item7.setQuantityCurrent(2.0);
        item7.setAlwaysInStock(true);
        item7.setMinimumQuantity(1.0);
        item7.setPriceInCent(1500L);
        item7.setBoughtAt("Winery");
        ItemCache itemCache7 = createWineItemCache();
        item7.setItemCache(itemCache7);
        item7.setLabels(Arrays.asList(label10));
        item7.setShoppingList(def);
        shoppingItemRepository.save(item7);

        // ShoppingItem 8
        ShoppingItem item8 = new ShoppingItem();
        item8.setQuantityCurrent(3.5);
        item8.setAlwaysInStock(false);
        item8.setMinimumQuantity(2.0);
        item8.setBoughtAt("Spice Bazaar");
        ItemCache itemCache8 = createSpiceItemCache();
        item8.setItemCache(itemCache8);
        item8.setLabels(Arrays.asList(label9));
        item8.setShoppingList(foodstuff);
        shoppingItemRepository.save(item8);

        // ShoppingItem 9
        ShoppingItem item9 = new ShoppingItem();
        item9.setQuantityCurrent(4.0);
        item9.setAlwaysInStock(true);
        item9.setMinimumQuantity(2.0);
        item9.setPriceInCent(800L);
        item9.setBoughtAt("Electronics Store");
        ItemCache itemCache9 = createElectronicsItemCache();
        item9.setItemCache(itemCache9);
        item9.setLabels(Arrays.asList(label5));
        item9.setShoppingList(tech);
        shoppingItemRepository.save(item9);

        // ShoppingItem 10
        ShoppingItem item10 = new ShoppingItem();
        item10.setQuantityCurrent(1.0);
        item10.setAlwaysInStock(true);
        item10.setMinimumQuantity(0.5);
        item10.setBoughtAt("Tea Emporium");
        ItemCache itemCache10 = createTeaItemCache();
        item10.setItemCache(itemCache10);
        item10.setLabels(Arrays.asList(label6, label3));
        item10.setShoppingList(foodstuff);
        shoppingItemRepository.save(item10);

        // Create Home Improvement Shopping Items
        ShoppingItem homeImprovementItem1 = new ShoppingItem();
        homeImprovementItem1.setQuantityCurrent(2.5);
        homeImprovementItem1.setAlwaysInStock(false);
        homeImprovementItem1.setBoughtAt("Home Improvement Store");
        ItemCache homeImprovementItemCache1 = createPaintItemCache();
        homeImprovementItem1.setItemCache(homeImprovementItemCache1);
        homeImprovementItem1.setLabels(Arrays.asList(label5, label2));
        homeImprovementItem1.setShoppingList(homeImprovements);
        shoppingItemRepository.save(homeImprovementItem1);

        ShoppingItem homeImprovementItem2 = new ShoppingItem();
        homeImprovementItem2.setQuantityCurrent(5.0);
        homeImprovementItem2.setAlwaysInStock(false);
        homeImprovementItem2.setBoughtAt("Hardware Store");
        ItemCache homeImprovementItemCache2 = createToolItemCache();
        homeImprovementItem2.setItemCache(homeImprovementItemCache2);
        homeImprovementItem2.setShoppingList(homeImprovements);
        shoppingItemRepository.save(homeImprovementItem2);

        ShoppingItem homeImprovementItem3 = new ShoppingItem();
        homeImprovementItem3.setQuantityCurrent(2.0);
        homeImprovementItem3.setAlwaysInStock(false);
        homeImprovementItem3.setBoughtAt("Home Improvement Center");
        ItemCache homeImprovementItemCache3 = createLightFixtureItemCache();
        homeImprovementItem3.setItemCache(homeImprovementItemCache3);
        homeImprovementItem3.setLabels(Arrays.asList(label10));
        homeImprovementItem3.setShoppingList(homeImprovements);
        shoppingItemRepository.save(homeImprovementItem3);

        ShoppingItem homeImprovementItem4 = new ShoppingItem();
        homeImprovementItem4.setQuantityCurrent(10.0);
        homeImprovementItem4.setAlwaysInStock(false);
        homeImprovementItem4.setBoughtAt("Home Decor Shop");
        ItemCache homeImprovementItemCache4 = createCurtainItemCache();
        homeImprovementItem4.setItemCache(homeImprovementItemCache4);
        homeImprovementItem4.setLabels(Arrays.asList(label4, label3));
        homeImprovementItem4.setShoppingList(homeImprovements);
        shoppingItemRepository.save(homeImprovementItem4);

        ShoppingItem homeImprovementItem5 = new ShoppingItem();
        homeImprovementItem5.setQuantityCurrent(1.0);
        homeImprovementItem5.setAlwaysInStock(false);
        homeImprovementItem5.setBoughtAt("Furniture Store");
        ItemCache homeImprovementItemCache5 = createFurnitureItemCache();
        homeImprovementItem5.setItemCache(homeImprovementItemCache5);
        homeImprovementItem5.setLabels(Arrays.asList(label1, label8));
        homeImprovementItem5.setShoppingList(homeImprovements);
        shoppingItemRepository.save(homeImprovementItem5);
    }

    private ItemCache createBakeryItemCache() {
        Unit pcs = unitRepository.findByName("pcs").orElseThrow();

        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Snacks");
        itemCache.setProductName("Butter Croissant");
        itemCache.setBrand("Local Bakery");
        itemCache.setQuantityTotal(10.0);
        itemCache.setDescription("Flaky and delicious");
        itemCache.setUnit(pcs);

        return itemCache;
    }

    private ItemCache createSeafoodItemCache() {
        Unit kg = unitRepository.findByName("kg").orElseThrow();

        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Foods");
        itemCache.setProductName("Atlantic Salmon");
        itemCache.setBrand("Fresh Fish Market");
        itemCache.setQuantityTotal(2.0);
        itemCache.setDescription("Sustainably sourced");
        itemCache.setUnit(kg);

        return itemCache;
    }

    private ItemCache createOrganicProduceItemCache() {
        Unit kg = unitRepository.findByName("kg").orElseThrow();

        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Foods");
        itemCache.setProductName("Fresh Green Kale");
        itemCache.setBrand("Organic Farm Stand");
        itemCache.setQuantityTotal(800.0);
        itemCache.setDescription("Rich in nutrients");
        itemCache.setUnit(kg);

        return itemCache;
    }

    private ItemCache createTechItemCache() {
        Unit pcs = unitRepository.findByName("pcs").orElseThrow();

        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Tech Gadgets");
        itemCache.setProductName("Premium AudioPods");
        itemCache.setBrand("Tech Store");
        itemCache.setQuantityTotal(20.0);
        itemCache.setDescription("Noise-canceling and comfortable");
        itemCache.setUnit(pcs);

        return itemCache;
    }

    private ItemCache createCheeseItemCache() {
        Unit g = unitRepository.findByName("g").orElseThrow();

        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Foods");
        itemCache.setProductName("French Camembert");
        itemCache.setBrand("Gourmet Cheese Shop");
        itemCache.setQuantityTotal(250.0);
        itemCache.setDescription("Creamy and flavorful");
        itemCache.setUnit(g);

        return itemCache;
    }

    private ItemCache createMeatItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Foods");
        itemCache.setProductName("Premium Beef");
        itemCache.setBrand("Local Butcher");
        itemCache.setQuantityTotal(1000.0);
        itemCache.setDescription("Grass-fed and tender");
        itemCache.setUnit(gram);
        return itemCache;
    }

    private ItemCache createWineItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Drinks");
        itemCache.setProductName("Cabernet Sauvignon");
        itemCache.setBrand("Winery");
        itemCache.setQuantityTotal(5.0);
        itemCache.setDescription("Aged for richness");
        itemCache.setUnit(liter);
        return itemCache;
    }

    private ItemCache createSpiceItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Spices");
        itemCache.setProductName("Ground Cumin");
        itemCache.setBrand("Spice Bazaar");
        itemCache.setQuantityTotal(200.0);
        itemCache.setDescription("Adds depth to dishes");
        itemCache.setUnit(gram);
        return itemCache;
    }

    private ItemCache createElectronicsItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Tech Gadgets");
        itemCache.setProductName("Ergonomic Mouse");
        itemCache.setBrand("Electronics Store");
        itemCache.setQuantityTotal(10.0);
        itemCache.setDescription("Comfortable and precise");
        itemCache.setUnit(pcs);
        return itemCache;
    }

    private ItemCache createTeaItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Drinks");
        itemCache.setProductName("Organic Green Tea Leaves");
        itemCache.setBrand("Tea Emporium");
        itemCache.setQuantityTotal(250.0);
        itemCache.setDescription("Soothing and refreshing");
        itemCache.setUnit(gram);
        return itemCache;
    }

    private ItemCache createPaintItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Tool Set");
        itemCache.setProductName("Interior Latex Paint");
        itemCache.setBrand("Home Improvement Store");
        itemCache.setQuantityTotal(3.5); // In liters
        itemCache.setDescription("High-quality paint for interior walls");
        itemCache.setUnit(liter);
        return itemCache;
    }

    private ItemCache createToolItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Tool Set");
        itemCache.setProductName("Professional Tool Kit");
        itemCache.setBrand("Hardware Store");
        itemCache.setQuantityTotal(6.0); // In pcs
        itemCache.setDescription("Essential tools for home repairs");
        itemCache.setUnit(pcs);
        return itemCache;
    }

    private ItemCache createLightFixtureItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Tool Set");
        itemCache.setProductName("Modern LED Ceiling Fixture");
        itemCache.setBrand("Home Improvement Center");
        itemCache.setQuantityTotal(2.0); // In pcs
        itemCache.setDescription("Elegant lighting solution for any room");
        itemCache.setUnit(pcs);
        return itemCache;
    }

    private ItemCache createCurtainItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Furniture");
        itemCache.setProductName("Blackout Curtains");
        itemCache.setBrand("Home Decor Shop");
        itemCache.setQuantityTotal(10.0); // In pcs
        itemCache.setDescription("Provides privacy and blocks sunlight");
        itemCache.setUnit(pcs);
        return itemCache;
    }

    private ItemCache createFurnitureItemCache() {
        ItemCache itemCache = new ItemCache();
        itemCache.setGeneralName("Furniture");
        itemCache.setProductName("Solid Wood Dining Table");
        itemCache.setBrand("Furniture Store");
        itemCache.setQuantityTotal(1.0); // In pcs
        itemCache.setDescription("Sturdy and stylish dining table");
        itemCache.setUnit(pcs);
        return itemCache;
    }

}