package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UnitRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

@Profile({"generateData", "test"})
@Component("ItemDataGenerator")
@DependsOn({"CleanDatabase", "StorageDataGenerator", "IngredientsDataGenerator", "UnitDataGenerator"})
public class ItemDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final UnitRepository unitRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public ItemDataGenerator(ItemRepository itemRepository,
                             UnitRepository unitRepository,
                             IngredientRepository ingredientRepository,
                             IngredientMapper ingredientMapper) {
        this.itemRepository = itemRepository;
        this.unitRepository = unitRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    @PostConstruct
    public void generateItems() {
        LOGGER.debug("generating Items");
        Unit pcs = unitRepository.findByName("pcs").orElseThrow();
        Unit g = unitRepository.findByName("g").orElseThrow();
        Unit ml = unitRepository.findByName("ml").orElseThrow();
        Unit l = unitRepository.findByName("l").orElseThrow();
        DigitalStorage storage = new DigitalStorage();
        storage.setStorageId(1L);

        DigitalStorageItem digitalStorageItem1 = generateDigitalStorageItem(
            null,
            "fruits",
            "Apples",
            "",
            10.0,
            10.0,
            pcs,
            LocalDate.now().plusDays(7),
            "",
            "Billa",
            storage,
            List.of(
                "Apple"
            )
        );

        DigitalStorageItem digitalStorageItem2 = generateDigitalStorageItem(
            null,
            "fruits",
            "Bananas",
            "",
            8.0,
            8.0,
            pcs,
            LocalDate.now().plusDays(2),
            "",
            "Billa",
            storage,
            List.of(
                "Banana"
            )
        );

        DigitalStorageItem digitalStorageItem3 = generateDigitalStorageItem(
            null,
            "fruits",
            "Pears",
            "",
            3.0,
            3.0,
            pcs,
            LocalDate.now().plusDays(3),
            "",
            "Hofer",
            storage,
            List.of(
                "Pear"
            )
        );

        DigitalStorageItem digitalStorageItem4 = generateDigitalStorageItem(
            null,
            "fruits",
            "Grapes",
            "",
            20.0,
            20.0,
            pcs,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "Grape"
            )
        );

        DigitalStorageItem digitalStorageItem5 = generateDigitalStorageItem(
            null,
            "fruits",
            "Plums",
            "",
            2.0,
            2.0,
            pcs,
            LocalDate.now().plusDays(10),
            "",
            "Billa",
            storage,
            List.of(
                "Plum"
            )
        );

        DigitalStorageItem digitalStorageItem6 = generateDigitalStorageItem(
            null,
            "vegetables",
            "Avocados",
            "Edeka Bio",
            5.0,
            5.0,
            pcs,
            LocalDate.now().plusMonths(6),
            "",
            "Hofer",
            storage,
            List.of("Avocado")
        );

        DigitalStorageItem digitalStorageItem7 = generateDigitalStorageItem(
            null,
            "vegetables",
            "Tomatoes",
            "",
            8.0,
            8.0,
            pcs,
            LocalDate.now().plusMonths(6),
            "",
            "Spar",
            storage,
            List.of("Tomatoes")
        );

        DigitalStorageItem digitalStorageItem8 = generateDigitalStorageItem(
            null,
            "vegetables",
            "Pumpkin",
            "",
            1.0,
            1.0,
            pcs,
            LocalDate.now().plusMonths(8),
            "",
            "Spar",
            storage,
            List.of("Tomatoes")
        );

        DigitalStorageItem digitalStorageItem9 = generateDigitalStorageItem(
            "7622300441937",
            "spreads",
            "Philadelphia",
            "Kraft",
            55.0,
            150.0,
            g,
            LocalDate.now().plusMonths(4),
            "",
            "Pagro",
            storage,
            List.of(
                "while milk",
                "cream",
                "milk protein preparation",
                "salt",
                "stabilizer (carob bean gum)"
            )
        );

        DigitalStorageItem digitalStorageItem10 = generateDigitalStorageAlwaysInStockItem(
            "8076809513722",
            "sauces",
            "Tomatosauce",
            "Barilla",
            20.0,
            190.0,
            g,
            LocalDate.now().plusMonths(5),
            "",
            "Hofer",
            storage,
            List.of(
                "tomato pulp",
                "tomato concentrate",
                "sunflower seed oil",
                "basil",
                "salt",
                "sugar",
                "natural flavoring"
            ),
            100L
        );

        DigitalStorageItem digitalStorageItem11 = generateDigitalStorageItem(
            "8076809513753",
            "sauces",
            "Tomatosauce",
            "Barilla",
            10.0,
            190.0,
            g,
            LocalDate.now().plusMonths(1),
            "",
            "Billa",
            storage,
            List.of(
                "sunflower seed oil",
                "basil",
                "cashew nuts",
                "grana padano dop cheese",
                "salt",
                "pecorino romano dop cheese",
                "sugar"
            )
        );

        DigitalStorageItem digitalStorageItem12 = generateDigitalStorageAlwaysInStockItem(
            "8076800195057",
            "noodles",
            "Spaghetti",
            "Barilla",
            450.0,
            500.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "durum wheat semolina",
                "water"
            ),
            100L
        );

        DigitalStorageItem digitalStorageItem13 = generateDigitalStorageAlwaysInStockItem(
            "8076802085738",
            "noodles",
            "Penne",
            "Barilla",
            500.0,
            500.0,
            g,
            LocalDate.now().plusMonths(7),
            "",
            "Spar",
            storage,
            List.of(
                "wheat semolina",
                "water"
            ),
            150L
        );

        DigitalStorageItem digitalStorageItem14 = generateDigitalStorageItem(
            null,
            "milk",
            "Milch",
            "Spar",
            2.0,
            2.0,
            l,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "milk"
            )
        );

        DigitalStorageItem digitalStorageItem15 = generateDigitalStorageItem(
            null,
            "milk",
            "Almond milk",
            "alpro",
            1000.0,
            1000.0,
            ml,
            LocalDate.now().plusMonths(3),
            "",
            "Spar",
            storage,
            List.of(
                "water",
                "almond",
                "sugar",
                "calcium",
                "sea salt",
                "stabilizer (locust bean gum, gellan gum)",
                "emulsifier (sunflower lecithin)",
                "vitamins (riboflavin (B2), B12, E, D2)"
            )
        );

        DigitalStorageItem digitalStorageItem16 = generateDigitalStorageItem(
            "5411188103387",
            "desserts",
            "Vanilla Yogurt",
            "alpro",
            500.0,
            500.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Spar",
            storage,
            List.of(
                "water",
                "hulled soya beans (7.9%)",
                "sugar",
                "glucose-fructose syrup",
                "vanilla (0.5%)",
                "tricalcium phosphate",
                "stabilizer (pectin)",
                "sea salt",
                "natural flavouring",
                "acidity regulator (citric acid)",
                "antioxidants (tocopherol-rich extract, fatty acid esters of ascorbic acid)",
                "vitamins (riboflavin (B2), B12, D2)"
            )
        );

        DigitalStorageItem digitalStorageItem17 = generateDigitalStorageItem(
            "8000500037560",
            "sweets",
            "Kinder Schokolade",
            "Ferrero",
            100.0,
            100.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Hofer",
            storage,
            List.of(
                "fine milk chocolate 40% (sugar, whole milk powder, cocoa butter, cocoa mass, emulsifier lecithin (soy), vanillin)",
                "sugar",
                "skimmed milk powder",
                "vegetable fats (palm, shea)",
                "concentrated butter",
                "emulsifier lecithin (soy)",
                "vanillin"
            )
        );

        DigitalStorageItem digitalStorageItem18 = generateDigitalStorageAlwaysInStockItem(
            null,
            "fruits",
            "Pineapple",
            "San Lucar",
            300.0,
            300.0,
            g,
            LocalDate.now().plusDays(3),
            "",
            "Billa",
            storage,
            List.of(
                "pineapple"
            ),
            150L
        );

        DigitalStorageItem digitalStorageItem19 = generateDigitalStorageAlwaysInStockItem(
            null,
            "fruits",
            "Raspberries",
            "San Lucar",
            200.0,
            200.0,
            g,
            LocalDate.now().plusDays(5),
            "",
            "Billa",
            storage,
            List.of(
                "raspberries"
            ),
            150L
        );

        DigitalStorageItem digitalStorageItem20 = generateDigitalStorageAlwaysInStockItem(
            null,
            "meat",
            "Chicken",
            "Bio Gasthof",
            400.0,
            400.0,
            g,
            LocalDate.now().plusDays(3),
            "",
            "Billa",
            storage,
            List.of(
                "chicken"
            ),
            200L
        );

        DigitalStorageItem digitalStorageItem21 = generateDigitalStorageAlwaysInStockItem(
            null,
            "meat",
            "Beef",
            "Bio Gasthof",
            350.0,
            350.0,
            g,
            LocalDate.now().plusDays(6),
            "",
            "Billa",
            storage,
            List.of(
                "beef"
            ),
            250L
        );

        DigitalStorageItem digitalStorageItem22 = generateDigitalStorageAlwaysInStockItem(
            null,
            "bread",
            "White bread",
            "",
            7.0,
            20.0,
            pcs,
            LocalDate.now().plusDays(8),
            "",
            "Hofer",
            storage,
            List.of(
                "yeast",
                "salt",
                "sugar",
                "oil",
                "flour"
            ),
            5L
        );

        DigitalStorageItem digitalStorageItem23 = generateDigitalStorageAlwaysInStockItem(
            null,
            "bread",
            "Wholewheat bread",
            "",
            16.0,
            45.0,
            pcs,
            LocalDate.now().plusDays(4),
            "",
            "Hofer",
            storage,
            List.of(
                "yeast",
                "salt",
                "sugar",
                "oil",
                "flour"
            ),
            3L
        );

        DigitalStorageItem digitalStorageItem24 = generateDigitalStorageAlwaysInStockItem(
            null,
            "sugar",
            "White sugar",
            "",
            400.0,
            400.0,
            g,
            LocalDate.now().plusDays(4),
            "",
            "Hofer",
            storage,
            List.of(
                "sugar",
                "sugar cane"
            ),
            100L
        );

        DigitalStorageItem digitalStorageItem25 = generateDigitalStorageAlwaysInStockItem(
            null,
            "sugar",
            "Brown sugar",
            "",
            600.0,
            600.0,
            g,
            LocalDate.now().plusDays(4),
            "",
            "Billa",
            storage,
            List.of(
                "sugar",
                "sugar cane"
            ),
            100L
        );

        DigitalStorageItem digitalStorageItem26 = generateDigitalStorageAlwaysInStockItem(
            null,
            "flour",
            "Flour",
            "",
            650.0,
            650.0,
            g,
            LocalDate.now().plusMonths(6),
            "",
            "Billa",
            storage,
            List.of(
                "flour"
            ),
            200L
        );

        DigitalStorageItem digitalStorageItem27 = generateDigitalStorageAlwaysInStockItem(
            null,
            "onion",
            "Onions",
            "",
            2.0,
            7.0,
            pcs,
            LocalDate.now().plusMonths(6),
            "",
            "Billa",
            storage,
            List.of(
                "onions"
            ),
            1L
        );

        DigitalStorageItem digitalStorageItem28 = generateDigitalStorageAlwaysInStockItem(
            null,
            "garlic",
            "Garlic",
            "",
            5.0,
            12.0,
            pcs,
            LocalDate.now().plusMonths(6),
            "",
            "Spar",
            storage,
            List.of(
                "garlic"
            ),
            3L
        );

        DigitalStorageItem digitalStorageItem29 = generateDigitalStorageAlwaysInStockItem(
            null,
            "vegetables",
            "Potatoes",
            "",
            350.0,
            450.0,
            pcs,
            LocalDate.now().plusMonths(6),
            "",
            "Billa",
            storage,
            List.of(
                "potatoes"
            ),
            100L
        );

        DigitalStorageItem digitalStorageItem30 = generateDigitalStorageAlwaysInStockItem(
            null,
            "vegetables",
            "Erbsen",
            "",
            500.0,
            500.0,
            g,
            LocalDate.now().plusMonths(9),
            "",
            "Spar",
            storage,
            List.of(
                "erbsen"
            ),
            50L
        );

        DigitalStorageItem digitalStorageItem31 = generateDigitalStorageAlwaysInStockItem(
            null,
            "oil",
            "Oil",
            "",
            700.0,
            800.0,
            g,
            LocalDate.now().plusMonths(9),
            "",
            "Spar",
            storage,
            List.of(
                "oil"
            ),
            200L
        );

        DigitalStorageItem digitalStorageItem32 = generateDigitalStorageAlwaysInStockItem(
            null,
            "vinegar",
            "Vinegar",
            "",
            500.0,
            500.0,
            g,
            LocalDate.now().plusMonths(9),
            "",
            "Hofer",
            storage,
            List.of(
                "vinegar"
            ),
            50L
        );

        DigitalStorageItem digitalStorageItem33 = generateDigitalStorageItem(
            null,
            "mushrooms",
            "Mushrooms",
            "",
            400.0,
            400.0,
            g,
            LocalDate.now().plusMonths(3),
            "",
            "Billa",
            storage,
            List.of(
                "mushrooms"
            )
        );

        DigitalStorageItem digitalStorageItem34 = generateDigitalStorageItem(
            null,
            "salt",
            "Salt",
            "",
            600.0,
            600.0,
            g,
            LocalDate.now().plusMonths(8),
            "",
            "Billa",
            storage,
            List.of(
                "salt"
            )
        );

        DigitalStorageItem digitalStorageItem35 = generateDigitalStorageItem(
            null,
            "pepper",
            "Pepper",
            "",
            500.0,
            500.0,
            g,
            LocalDate.now().plusMonths(8),
            "",
            "Spar",
            storage,
            List.of(
                "pepper"
            )
        );

        LOGGER.debug("saving digitalStorageItem {}", digitalStorageItem1);
        itemRepository.saveAll(
            List.of(
                digitalStorageItem1,
                digitalStorageItem2,
                digitalStorageItem3,
                digitalStorageItem4,
                digitalStorageItem5,
                digitalStorageItem6,
                digitalStorageItem7,
                digitalStorageItem8,
                digitalStorageItem9,
                digitalStorageItem10,
                digitalStorageItem11,
                digitalStorageItem12,
                digitalStorageItem13,
                digitalStorageItem14,
                digitalStorageItem15,
                digitalStorageItem16,
                digitalStorageItem17,
                digitalStorageItem18,
                digitalStorageItem19,
                digitalStorageItem20,
                digitalStorageItem21,
                digitalStorageItem22,
                digitalStorageItem23,
                digitalStorageItem24,
                digitalStorageItem25,
                digitalStorageItem26,
                digitalStorageItem27,
                digitalStorageItem28,
                digitalStorageItem29,
                digitalStorageItem30,
                digitalStorageItem31,
                digitalStorageItem32,
                digitalStorageItem33,
                digitalStorageItem34,
                digitalStorageItem35
            )
        );

    }

    private DigitalStorageItem generateDigitalStorageItem(String ean,
                                                          String generalName,
                                                          String productName,
                                                          String brand,
                                                          Double quantityCurrent,
                                                          Double quantityTotal,
                                                          Unit unit,
                                                          LocalDate expireDate,
                                                          String description,
                                                          String boughtAt,
                                                          DigitalStorage digitalStorage,
                                                          List<String> ingredientList) {
        DigitalStorageItem digitalStorageItem = new DigitalStorageItem();
        digitalStorageItem.getItemCache().setEan(ean);
        digitalStorageItem.getItemCache().setGeneralName(generalName);
        digitalStorageItem.getItemCache().setProductName(productName);
        digitalStorageItem.getItemCache().setBrand(brand);
        digitalStorageItem.setQuantityCurrent(quantityCurrent);
        digitalStorageItem.getItemCache().setQuantityTotal(quantityTotal);
        digitalStorageItem.getItemCache().setUnit(unit);
        digitalStorageItem.setExpireDate(expireDate);
        digitalStorageItem.getItemCache().setDescription(description);
        digitalStorageItem.setBoughtAt(boughtAt);
        digitalStorageItem.setDigitalStorage(digitalStorage);
        digitalStorageItem.setIngredientList(findIngredientsAndCreateMissing(
            ingredientList.stream()
                .map(ingredientName -> new IngredientDto(null, ingredientName))
                .toList()
        ));
        return digitalStorageItem;
    }

    private DigitalStorageItem generateDigitalStorageAlwaysInStockItem(String ean,
                                                                       String generalName,
                                                                       String productName,
                                                                       String brand,
                                                                       Double quantityCurrent,
                                                                       Double quantityTotal,
                                                                       Unit unit,
                                                                       LocalDate expireDate,
                                                                       String description,
                                                                       String boughtAt,
                                                                       DigitalStorage digitalStorage,
                                                                       List<String> ingredientList,
                                                                       Long minimumQuantity) {
        DigitalStorageItem digitalStorageItem = new AlwaysInStockDigitalStorageItem();
        digitalStorageItem.getItemCache().setEan(ean);
        digitalStorageItem.getItemCache().setGeneralName(generalName);
        digitalStorageItem.getItemCache().setProductName(productName);
        digitalStorageItem.getItemCache().setBrand(brand);
        digitalStorageItem.setQuantityCurrent(quantityCurrent);
        digitalStorageItem.getItemCache().setQuantityTotal(quantityTotal);
        digitalStorageItem.getItemCache().setUnit(unit);
        digitalStorageItem.setExpireDate(expireDate);
        digitalStorageItem.getItemCache().setDescription(description);
        digitalStorageItem.setBoughtAt(boughtAt);
        digitalStorageItem.setDigitalStorage(digitalStorage);
        digitalStorageItem.setIngredientList(findIngredientsAndCreateMissing(
            ingredientList.stream()
                .map(ingredientName -> new IngredientDto(null, ingredientName))
                .toList()
        ));
        digitalStorageItem.setMinimumQuantity(minimumQuantity);

        return digitalStorageItem;
    }

    public List<Ingredient> findIngredientsAndCreateMissing(List<IngredientDto> ingredientDtoList) {
        if (ingredientDtoList == null) {
            return List.of();
        }
        List<Ingredient> ingredientList = ingredientRepository.findAllByTitleIsIn(
            ingredientDtoList.stream()
                .map(IngredientDto::name)
                .toList()
        );

        List<IngredientDto> missingIngredients = ingredientDtoList.stream()
            .filter(ingredientDto ->
                ingredientList.stream()
                    .noneMatch(ingredient ->
                        ingredient.getTitle().equals(ingredientDto.name())
                    )
            ).toList();

        if (!missingIngredients.isEmpty()) {
            List<Ingredient> createdIngredients = ingredientRepository.saveAll(
                ingredientMapper.dtoListToEntityList(missingIngredients)
            );
            ingredientList.addAll(createdIngredients);
        }
        return ingredientList;
    }
}
