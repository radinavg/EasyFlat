package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookbookDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.CookingSteps;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.cooking.Step;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CookbookMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeIngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UnitMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlwaysInStockDigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Cookbook;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CookbookRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeIngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.authorization.Authorization;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.CookbookValidator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.RecipeValidator;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CookingServiceImpl implements CookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RestTemplate restTemplate;
    private final DigitalStorageServiceImpl digitalStorageService;
    private final String apiKey = "3b683601a4f44cd38d367ab0a1db032d";
    private final RecipeSuggestionRepository repository;
    private final RecipeIngredientService ingredientService;
    private final RecipeIngredientMapper ingredientMapper;
    private final ItemRepository itemRepository;
    private final RecipeMapper recipeMapper;
    private final RecipeIngredientMapper recipeIngredientMapper;
    private final UnitService unitService;
    private final UnitMapper unitMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final RecipeValidator recipeValidator;
    private final CookbookValidator cookbookValidator;
    private final SharedFlatService sharedFlatService;
    private final Authorization authorization;
    private final CookbookMapper cookbookMapper;
    private final CookbookRepository cookbookRepository;
    private final ShoppingListService shoppingListService;
    private final ShoppingListMapper shoppingListMapper;
    private final DigitalStorageMapper digitalStorageMapper;

    private Translator translator;
    private final String translateKey = "99218f60-385f-59ea-55ce-513e8cec1469:fx";

    private final AuthService authService;

    private final String apiUrlNew = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/findByIngredients";


    public CookingServiceImpl(RestTemplate restTemplate,
                              RecipeSuggestionRepository repository,
                              DigitalStorageServiceImpl digitalStorageService,
                              RecipeIngredientService ingredientService,
                              RecipeIngredientMapper ingredientMapper,
                              ItemRepository itemRepository,
                              RecipeMapper recipeMapper,
                              RecipeIngredientMapper recipeIngredientMapper,
                              UnitService unitService,
                              UnitMapper unitMapper,
                              ItemService itemService,
                              ItemMapper itemMapper, RecipeValidator recipeValidator,
                              CookbookValidator cookbookValidator, SharedFlatService sharedFlatService,
                              Authorization authorization, CookbookMapper cookbookMapper,
                              CookbookRepository cookbookRepository, UserService userService,
                              CustomUserDetailService customUserDetailService,
                              ShoppingListService shoppingListService,
                              ShoppingListMapper shoppingListMapper,
                              DigitalStorageMapper digitalStorageMapper, AuthService authService) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.ingredientMapper = ingredientMapper;
        this.recipeMapper = recipeMapper;
        this.recipeIngredientMapper = recipeIngredientMapper;
        this.unitService = unitService;
        this.unitMapper = unitMapper;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.recipeValidator = recipeValidator;
        this.cookbookValidator = cookbookValidator;
        this.sharedFlatService = sharedFlatService;
        this.authorization = authorization;
        this.cookbookMapper = cookbookMapper;
        this.cookbookRepository = cookbookRepository;
        this.shoppingListService = shoppingListService;
        this.shoppingListMapper = shoppingListMapper;
        this.digitalStorageMapper = digitalStorageMapper;
        this.itemRepository = itemRepository;
        this.authService = authService;
    }

    @Override
    public List<RecipeSuggestionDto> getRecipeSuggestion(String type)
        throws ConflictException, AuthorizationException, AuthenticationException, DeepLException, InterruptedException {
        LOGGER.trace("getRecipeSuggestions");
        ApplicationUser user = authService.getUserFromToken();

        List<ItemDto> items = itemRepository.findAllByDigitalStorage_StorageId(user.getSharedFlat().getDigitalStorage().getStorageId()).stream().map(itemMapper::entityToDto).toList();
        if (items.isEmpty()) {
            throw new ConflictException("Storage is empty", List.of("Please add some ingredients"));
        }

        String requestString = getRequestStringForRecipeSearch(items);
        ResponseEntity<List<RecipeDto>> exchange = restTemplate.exchange(requestString, HttpMethod.GET, getHttpEntity(), new ParameterizedTypeReference<List<RecipeDto>>() {
        });


        List<RecipeDto> recipes = new LinkedList<>();
        if (exchange.getBody() != null) {
            recipes.addAll(exchange.getBody());
        }


        List<RecipeSuggestionDto> recipeInfo = new LinkedList<>();

        for (RecipeDto recipeDto : recipes) {
            String newReqString = getNewReqStringForInformation(recipeDto.id());
            LOGGER.debug("Sending request to Spoonacular API for recipe information.");
            ResponseEntity<RecipeSuggestionDto> response = restTemplate.exchange(newReqString, HttpMethod.GET, getHttpEntity(), new ParameterizedTypeReference<RecipeSuggestionDto>() {
            });
            recipeInfo.add(response.getBody());

        }


        List<RecipeSuggestionDto> toReturn = getRecipeSuggestionDtos(recipeInfo, recipes);

        if (type != null) {
            toReturn = filterSuggestions(toReturn, type);
        }

        toReturn = saveUnitsAlsUnits(toReturn);
        toReturn = setMatchedIngredients(toReturn);

        return toReturn;
    }

    @Override
    public RecipeDetailDto getRecipeDetails(Long recipeId) {
        LOGGER.trace("getRecipeDetails for Recipe with ID {}", recipeId);
        String reqString = getNewReqStringForInformation(recipeId);
        ResponseEntity<RecipeDetailDto> response = restTemplate.exchange(reqString, HttpMethod.GET, getHttpEntity(), new ParameterizedTypeReference<RecipeDetailDto>() {
        });

        String stepsReqString = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + recipeId + "/analyzedInstructions" + "?stepBreakdown=true";
        ResponseEntity<List<CookingSteps>> responseSteps = restTemplate.exchange(stepsReqString, HttpMethod.GET, getHttpEntity(), new ParameterizedTypeReference<List<CookingSteps>>() {
        });

        RecipeDetailDto recipeDetailDto = response.getBody();
        if (response.getBody() != null) {
            recipeDetailDto = filterRecipeDetailIngredients(response.getBody());
        }
        CookingSteps steps = null;

        if (responseSteps.getBody() != null && !responseSteps.getBody().isEmpty()) {
            steps = responseSteps.getBody().get(0);
        }
        RecipeDetailDto withUnits = saveUnitAlsUnitsForDetail(recipeDetailDto);
        List<RecipeIngredientDto> matchedIngredients = getMatchedIngredients(withUnits.extendedIngredients());

        if (recipeDetailDto != null) {
            return new RecipeDetailDto(
                recipeId,
                recipeDetailDto.title(),
                recipeDetailDto.servings(),
                recipeDetailDto.readyInMinutes(),
                matchedIngredients,
                recipeDetailDto.summary(),
                steps
            );
        }
        return null;
    }

    @Override
    public List<Cookbook> findAllCookbooks() {
        LOGGER.trace("findAllCookbooks");
        ApplicationUser applicationUser = this.authService.getUserFromToken();

        return cookbookRepository.findBySharedFlatIs(applicationUser.getSharedFlat());
    }

    @Override
    public List<RecipeSuggestionDto> getCookbook() throws ValidationException, AuthorizationException {
        LOGGER.trace("getCookbook");
        List<RecipeSuggestionDto> recipesDto = new LinkedList<>();
        Long cookbookId = this.getCookbookIdForUser();
        Cookbook cookbook = cookbookRepository.findById(cookbookId).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));
        List<RecipeSuggestion> recipes = cookbook.getRecipes();
        for (RecipeSuggestion recipe : recipes) {
            RecipeSuggestionDto toAdd = recipeMapper.entityToRecipeSuggestionDto(recipe);
            List<RecipeIngredientDto> matchedIngr = getMatchedIngredients(toAdd.extendedIngredients());
            toAdd = toAdd.withExtendedIngredients(matchedIngr);
            recipesDto.add(toAdd);
        }

        return recipesDto;
    }

    @Override
    @Transactional
    public RecipeSuggestion createCookbookRecipe(RecipeSuggestionDto recipe) throws AuthorizationException, ConflictException, ValidationException {
        LOGGER.trace("createCookbookRecipe : {}", recipe);
        if (recipe.id() != null) {
            RecipeDetailDto recipeWithSteps = getRecipeDetails(recipe.id());
            StringBuilder summary = new StringBuilder(recipe.summary());
            //Remove HTML tags
            summary = new StringBuilder(Jsoup.parse(summary.toString()).text());
            if (recipeWithSteps.steps() != null) {
                for (Step step : recipeWithSteps.steps().steps()) {
                    summary.append("\r\n" + "Step ").append(step.number()).append(": ").append(step.step());
                }
            }


            recipe = recipe.withId(null).withSummaryAndWithoutMissingIngredients(summary.toString());

        }
        recipeValidator.validateForCreate(recipe);
        List<RecipeIngredient> ingredientList = ingredientService.createAll(recipe.extendedIngredients());
        RecipeSuggestion recipeEntity = recipeMapper.dtoToEntity(recipe, ingredientList);
        Long cookbookId = this.getCookbookIdForUser();
        Cookbook cookbook = cookbookRepository.findById(cookbookId).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));
        recipeEntity.setCookbook(cookbook);
        RecipeSuggestion createdRecipe = repository.save(recipeEntity);
        createdRecipe.setExtendedIngredients(ingredientList);

        return createdRecipe;
    }

    @Override
    public RecipeSuggestionDto getCookbookRecipe(Long id) {
        LOGGER.trace("getCookbookRecipe with id: {}", id);
        if (id == null) {
            return null;
        }
        RecipeSuggestion recipe = repository.findById(id).orElseThrow(() -> new NotFoundException("Id does not exist in database!"));
        RecipeSuggestionDto recipeToReturn = recipeMapper.entityToRecipeSuggestionDto(recipe);
        List<RecipeIngredientDto> matchedIngr = getMatchedIngredients(recipeToReturn.extendedIngredients());

        return recipeToReturn.withExtendedIngredients(matchedIngr);
    }

    @Override
    @Transactional
    public RecipeSuggestion updateCookbookRecipe(RecipeSuggestionDto recipe) throws ValidationException, AuthorizationException {
        LOGGER.trace("updateCookbookRecipe: {}", recipe);
        recipeValidator.validateForUpdate(recipe);
        RecipeSuggestion oldRecipe = repository.findById(recipe.id()).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));

        Long cookbookId = this.getCookbookIdForUser();
        Cookbook cookbook = cookbookRepository.findById(cookbookId).orElseThrow(() -> new NotFoundException("Given Id does not exist in the Database!"));

        oldRecipe.setTitle(recipe.title());
        oldRecipe.setSummary(recipe.summary());
        oldRecipe.setReadyInMinutes(recipe.readyInMinutes());
        oldRecipe.setServings(recipe.servings());
        oldRecipe.setCookbook(cookbook);

        List<RecipeIngredient> ingredientList = ingredientService.createAll(recipe.extendedIngredients());
        oldRecipe.getExtendedIngredients().clear();
        oldRecipe.getExtendedIngredients().addAll(ingredientList);


        RecipeSuggestion updatedRecipe = repository.save(oldRecipe);
        updatedRecipe.setExtendedIngredients(ingredientList);

        return updatedRecipe;
    }

    @Override
    @Transactional
    public RecipeSuggestion deleteCookbookRecipe(Long id) throws AuthorizationException {
        LOGGER.trace("deleteCookbookRecipe with id: {}", id);
        RecipeSuggestionDto deletedRecipe = this.getCookbookRecipe(id);
        this.getCookbookIdForUser();
        RecipeSuggestion toDelete = recipeMapper.dtoToEntity(deletedRecipe, recipeIngredientMapper.dtoListToEntityList(deletedRecipe.extendedIngredients()));
        repository.deleteById(toDelete.getId());

        return recipeMapper.dtoToEntity(deletedRecipe, recipeIngredientMapper.dtoListToEntityList(deletedRecipe.extendedIngredients()));
    }

    @Override
    public RecipeSuggestionDto getMissingIngredients(Long id) {
        LOGGER.trace("getMissingIngredients for recipe with id: {}", id);
        ApplicationUser user = this.authService.getUserFromToken();
        DigitalStorage digitalStorageOfUser = user.getSharedFlat().getDigitalStorage();
        boolean fromApi = false;

        RecipeSuggestion recipeEntity;
        try {
            recipeEntity = repository.findById(id).orElseThrow(() -> {
                throw new NotFoundException();
            });
        } catch (NotFoundException e) {
            recipeEntity = getRecipeFromApi(id);
            fromApi = true;
        }

        RecipeSuggestionDto recipeSuggestionDto = filterRecipeSuggestionIngredients(recipeMapper.entityToRecipeSuggestionDto(recipeEntity));
        if (fromApi) {
            List<RecipeSuggestionDto> recipes = this.saveUnitsAlsUnits(List.of(recipeSuggestionDto));
            recipeSuggestionDto = recipes.get(0);
        }
        if (recipeSuggestionDto != null) {
            List<RecipeIngredientDto> missingIngredients = new LinkedList<>();
            List<RecipeIngredientDto> matched = this.getMatchedIngredients(recipeSuggestionDto.extendedIngredients());
            recipeSuggestionDto = recipeSuggestionDto.withExtendedIngredients(matched);
            for (RecipeIngredientDto ingredient : recipeSuggestionDto.extendedIngredients()) {
                List<DigitalStorageItem> items = itemRepository.findAllByDigitalStorage_StorageId(digitalStorageOfUser.getStorageId());
                if (items.isEmpty()) {
                    missingIngredients.add(ingredient);
                    continue;
                }

                Unit ingredientUnit = unitMapper.unitDtoToEntity(ingredient.unitEnum());

                try {
                    Double ingAmountMin = unitService.convertUnits(ingredientUnit, getMinUnit(ingredientUnit), ingredient.amount());
                    Double itemQuantityTotal = getItemQuantityTotalInMinQuantity(items, ingredient);

                    if (ingAmountMin > itemQuantityTotal) {
                        if (ingredientUnit.getConvertFactor() == null) {
                            ingredientUnit.setConvertFactor(1L);
                        }
                        if (ingAmountMin < ingredientUnit.getConvertFactor()) {

                            RecipeIngredientDto newIngredient = new RecipeIngredientDto(
                                ingredient.id(),
                                ingredient.name(),
                                getMinUnit(ingredientUnit).getName(),
                                unitMapper.entityToUnitDto(getMinUnit(ingredientUnit)),
                                ingAmountMin - itemQuantityTotal,
                                false,
                                ingredient.autoMatched(),
                                ingredient.haveWithDifferentUnits(),
                                ingredient.realName(),
                                ingredient.matchedItem()
                            );
                            missingIngredients.add(newIngredient);
                        } else {

                            Double updatedQuantity = unitService.convertUnits(getMinUnit(ingredientUnit), ingredientUnit, ingAmountMin - itemQuantityTotal);
                            RecipeIngredientDto newIngredient = new RecipeIngredientDto(
                                ingredient.id(),
                                ingredient.name(),
                                unitMapper.unitDtoToEntity(ingredient.unitEnum()).getName(),
                                unitMapper.entityToUnitDto(unitMapper.unitDtoToEntity(ingredient.unitEnum())),
                                updatedQuantity,
                                false,
                                ingredient.autoMatched(),
                                ingredient.haveWithDifferentUnits(),
                                ingredient.realName(),
                                ingredient.matchedItem()
                            );
                            missingIngredients.add(newIngredient);
                        }
                    }


                } catch (ValidationException | ConflictException e) {
                    throw new RuntimeException(e);
                }


            }
            RecipeSuggestionDto newRecipe = new RecipeSuggestionDto(recipeSuggestionDto.id(), recipeSuggestionDto.title(), recipeSuggestionDto.servings(),
                recipeSuggestionDto.readyInMinutes(), recipeSuggestionDto.extendedIngredients(), recipeSuggestionDto.summary(),
                missingIngredients, recipeSuggestionDto.dishTypes());
            recipeSuggestionDto = newRecipe;
        }
        return recipeSuggestionDto;

    }

    @Override
    public RecipeSuggestionDto cookRecipe(RecipeSuggestionDto recipeToCook) throws ValidationException, ConflictException, AuthorizationException {
        LOGGER.trace("cookRecipe: {}", recipeToCook);

        recipeValidator.validateForCook(recipeToCook);
        ApplicationUser user = authService.getUserFromToken();
        Long storageId = user.getSharedFlat().getDigitalStorage().getStorageId();
        List<RecipeIngredientDto> ingredientToRemoveFromStorage = recipeToCook.extendedIngredients();
        for (RecipeIngredientDto recipeIngredientDto : ingredientToRemoveFromStorage) {
            List<DigitalStorageItem> digitalStorageItems = itemRepository.findAllByDigitalStorage_StorageId(storageId);
            Unit ingredientUnit = unitMapper.unitDtoToEntity(recipeIngredientDto.unitEnum());
            Double ingAmountMin = unitService.convertUnits(ingredientUnit, getMinUnit(ingredientUnit), recipeIngredientDto.amount());
            List<DigitalStorageItem> itemsWithMinUnits = minimizeUnits(digitalStorageItems);
            for (int i = 0; i < itemsWithMinUnits.size(); i++) {
                if (digitalStorageItems.get(i).getItemCache().getProductName().equals(recipeIngredientDto.name())) {
                    if (unitService.areUnitsComparable(digitalStorageItems.get(i).getItemCache().getUnit(), unitMapper.unitDtoToEntity(recipeIngredientDto.unitEnum()))) {

                        DigitalStorageItem digitalStorageItem = itemsWithMinUnits.get(i);

                        if (digitalStorageItem.getQuantityCurrent() >= ingAmountMin) {
                            if (digitalStorageItem.getItemCache().getUnit().equals(digitalStorageItems.get(i).getItemCache().getUnit())) {
                                ItemDto updatedItem = itemMapper.entityToDto(digitalStorageItem).withUpdatedQuantity(truncateToDecimalPlaces(digitalStorageItem.getQuantityCurrent() - ingAmountMin, 2));
                                itemService.update(updatedItem);
                            } else {
                                Double updatedQuantity = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), digitalStorageItems.get(i).getItemCache().getUnit(), digitalStorageItem.getQuantityCurrent() - ingAmountMin);
                                updatedQuantity = truncateToDecimalPlaces(updatedQuantity, 2);
                                digitalStorageItem.getItemCache().setUnit(digitalStorageItems.get(i).getItemCache().getUnit());
                                ItemDto updatedItem = itemMapper.entityToDto(digitalStorageItem).withUpdatedQuantity(updatedQuantity);
                                itemService.update(updatedItem);
                            }
                            break;
                        } else {
                            ingAmountMin -= digitalStorageItem.getQuantityCurrent();
                            digitalStorageItem.getItemCache().setUnit(digitalStorageItems.get(i).getItemCache().getUnit());
                            ItemDto updatedItem = itemMapper.entityToDto(digitalStorageItem).withUpdatedQuantity(0.0);
                            itemService.update(updatedItem);

                        }
                    }
                }
            }
        }
        return recipeToCook;
    }

    @Override
    public RecipeSuggestionDto addToShoppingList(RecipeSuggestionDto recipeToCook)
        throws AuthenticationException, ValidationException, ConflictException, AuthorizationException {
        LOGGER.trace("addToShoppingList: {}", recipeToCook);
        ShoppingList shoppingList = shoppingListService.getShoppingListByName("Shopping List (Default)").orElseThrow(() -> new NotFoundException("Given Id does not exists in the Database!"));
        ShoppingListDto shoppingListDto = shoppingListMapper.entityToDto(shoppingList);
        Long storageId = this.getStorageIdForUser();
        DigitalStorage storage = digitalStorageService.findById(storageId);
        DigitalStorageDto storageDto = digitalStorageMapper.entityToDto(storage);
        List<RecipeIngredientDto> ingredientDtoList = recipeToCook.missedIngredients();
        for (RecipeIngredientDto ingredient : recipeToCook.missedIngredients()) {
            ShoppingItemDto newShoppingItem = new ShoppingItemDto(null, null, ingredient.name(), ingredient.name(), ingredient.name(), ingredient.amount(), ingredient.amount(),
                ingredient.unitEnum(), null, null, false, ingredient.amount(), null, null, null, shoppingListDto);
            shoppingListService.createShoppingItem(newShoppingItem);
        }
        return recipeToCook;
    }

    @Override
    public RecipeIngredientDto unMatchIngredient(String ingredientName) {
        LOGGER.trace("unMatchIngredient with name: {}", ingredientName);
        return ingredientService.unMatchIngredient(ingredientName);
    }

    private List<RecipeSuggestionDto> filterSuggestions(List<RecipeSuggestionDto> recipeSuggestions, String type) {
        List<String> filterTypes = new ArrayList<>();
        if (type.equals("breakfast")) {
            filterTypes.add("breakfast");
            filterTypes.add("snack");
            filterTypes.add("dessert");
            filterTypes.add("brunch");
            filterTypes.add("morning meal");
        }
        if (type.equals("main dish")) {
            filterTypes.add("main course");
            filterTypes.add("lunch");
            filterTypes.add("dinner");
            filterTypes.add("soup");

        }
        if (type.equals("side dish")) {
            filterTypes.add("salad");
            filterTypes.add("side dish");
            filterTypes.add("appetizer");
            filterTypes.add("snack");
            filterTypes.add("fingerfood");
            filterTypes.add("marinade");
            filterTypes.add("starter");
            filterTypes.add("antipasti");
        } else if (type.equals("all types")) {
            return recipeSuggestions;
        }

        Map<String, RecipeSuggestionDto> results = new HashMap<>();


        for (String filter : filterTypes) {
            loop2:
            for (RecipeSuggestionDto suggestionDto : recipeSuggestions) {
                for (String typeOfDish : suggestionDto.dishTypes()) {
                    if (filter.equals(typeOfDish)) {
                        results.put(suggestionDto.title(), suggestionDto);
                        break;
                    }
                }
            }
        }
        List<RecipeSuggestionDto> recipesToRet = new ArrayList<>();
        for (Map.Entry<String, RecipeSuggestionDto> recipe : results.entrySet()) {
            recipesToRet.add(recipe.getValue());
        }

        return recipesToRet;
    }

    private List<RecipeSuggestionDto> getRecipeSuggestionDtos(List<RecipeSuggestionDto> response, List<RecipeDto> recipes) {
        List<RecipeSuggestionDto> toReturn = new LinkedList<>();

        if (response != null) {
            for (int i = 0; i < response.size(); i++) {
                RecipeDto hereWeHaveMissIng = recipes.get(i);
                RecipeSuggestionDto details = response.get(i);

                RecipeSuggestionDto toAdd = new RecipeSuggestionDto(
                    details.id(),
                    details.title(),
                    details.servings(),
                    details.readyInMinutes(),
                    details.extendedIngredients(),
                    details.summary(),
                    hereWeHaveMissIng.missedIngredients(),
                    details.dishTypes()
                );
                toReturn.add(filterRecipeSuggestionIngredients(toAdd));
            }
        }
        return toReturn;
    }

    private RecipeSuggestionDto filterRecipeSuggestionIngredients(RecipeSuggestionDto recipeSuggestion) {
        List<RecipeIngredientDto> recipeIngredientDtos = recipeSuggestion.extendedIngredients();
        Map<String, RecipeIngredientDto> recipeIngredientDtoMap = new HashMap<>();


        for (RecipeIngredientDto recipeIngredient : recipeIngredientDtos) {
            String normalizedIngredientName = removeSpecialCharacters(recipeIngredient.name());
            RecipeIngredientDto ingredientUpdated = recipeIngredient.withName(normalizedIngredientName);

            recipeIngredientDtoMap.put(normalizedIngredientName, ingredientUpdated);
        }
        List<RecipeIngredientDto> recipeIngredientDtoListNew = new ArrayList<>(recipeIngredientDtoMap.values());

        return recipeSuggestion.withExtendedIngredients(recipeIngredientDtoListNew);
    }

    public RecipeDetailDto filterRecipeDetailIngredients(RecipeDetailDto recipeDetailDto) {
        List<RecipeIngredientDto> recipeIngredientDtos = recipeDetailDto.extendedIngredients();
        Map<String, RecipeIngredientDto> recipeIngredientDtoMap = new HashMap<>();

        for (RecipeIngredientDto recipeIngredient : recipeIngredientDtos) {
            String normalizedIngredientName = removeSpecialCharacters(recipeIngredient.name());
            RecipeIngredientDto ingredientUpdated = recipeIngredient.withName(normalizedIngredientName);
            recipeIngredientDtoMap.put(normalizedIngredientName, ingredientUpdated);
        }

        List<RecipeIngredientDto> recipeIngredientDtoListNew = new ArrayList<>(recipeIngredientDtoMap.values());

        // Return a new RecipeDetailDto with updated ingredients
        return recipeDetailDto.withExtendedIngredients(recipeIngredientDtoListNew);
    }

    public static String removeSpecialCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s-]");

        return pattern.matcher(input).replaceAll("");
    }

    private List<RecipeIngredientDto> getMatchedIngredients(List<RecipeIngredientDto> ingredientDtos) {

        ApplicationUser user = authService.getUserFromToken();
        List<DigitalStorageItem> itemsInStorage = itemRepository.findAllByDigitalStorage_StorageId(user.getSharedFlat().getDigitalStorage().getStorageId());

        List<RecipeIngredientDto> updatedIngredients = new LinkedList<>();

        loop2:
        for (RecipeIngredientDto recipeIngredient : ingredientDtos) {
            boolean matched = false;
            boolean haveWithDivUnits = false;

            ingredientService.unMatchIngredient(recipeIngredient.name());
            //case auto Match
            for (DigitalStorageItem digitalStorageItem : itemsInStorage) {

                if (recipeIngredient.name().equals(digitalStorageItem.getItemCache().getProductName())) {
                    haveWithDivUnits = true;
                }


                Unit recipeIngredientUnit = unitMapper.unitDtoToEntity(recipeIngredient.unitEnum());
                Unit digitalStorageItemUnit = digitalStorageItem.getItemCache().getUnit();
                if (digitalStorageItem.getItemCache().getProductName().equals(recipeIngredient.name()) && unitService.areUnitsComparable(recipeIngredientUnit, digitalStorageItemUnit)
                    && recipeIngredient.realName() == null) {
                    RecipeIngredientDto updatedIngredient = new RecipeIngredientDto(recipeIngredient.id(),
                        digitalStorageItem.getItemCache().getProductName(),
                        recipeIngredient.unit(),
                        recipeIngredient.unitEnum(),
                        recipeIngredient.amount(),
                        true,
                        true,
                        recipeIngredient.haveWithDifferentUnits(),
                        recipeIngredient.name(),
                        itemMapper.entityToDto(digitalStorageItem));
                    updatedIngredients.add(updatedIngredient);
                    continue loop2;
                } else if (digitalStorageItem.getItemCache().getProductName().equals(recipeIngredient.name()) && unitService.areUnitsComparable(recipeIngredientUnit, digitalStorageItemUnit)
                    //case Yet Matched
                    && !recipeIngredient.name().equals(recipeIngredient.realName())) {
                    for (AlternativeName alternativeName : digitalStorageItem.getItemCache().getAlternativeNames()) {
                        if (alternativeName.getName().equals(recipeIngredient.realName())) {
                            RecipeIngredientDto updatedIngredient = new RecipeIngredientDto(recipeIngredient.id(),
                                digitalStorageItem.getItemCache().getProductName(),
                                recipeIngredient.unit(),
                                recipeIngredient.unitEnum(),
                                recipeIngredient.amount(),
                                true,
                                false,
                                recipeIngredient.haveWithDifferentUnits(),
                                recipeIngredient.realName(),
                                itemMapper.entityToDto(digitalStorageItem));
                            updatedIngredients.add(updatedIngredient);
                            continue loop2;
                        }
                    }
                }
                //case Not Matched Yet
                for (AlternativeName alternativeName : digitalStorageItem.getItemCache().getAlternativeNames()) {
                    if (alternativeName.getName().equals(recipeIngredient.name())) {
                        RecipeIngredientDto updatedIngredient = new RecipeIngredientDto(recipeIngredient.id(),
                            digitalStorageItem.getItemCache().getProductName(),
                            recipeIngredient.unit(),
                            recipeIngredient.unitEnum(),
                            recipeIngredient.amount(),
                            true,
                            false,
                            recipeIngredient.haveWithDifferentUnits(),
                            recipeIngredient.name(),
                            itemMapper.entityToDto(digitalStorageItem));
                        updatedIngredients.add(updatedIngredient);
                        continue loop2;
                    }
                }
            }
            updatedIngredients.add(recipeIngredient.updateHaveWithDifferentIngredients(haveWithDivUnits));
        }
        return updatedIngredients;
    }

    private List<RecipeSuggestionDto> setMatchedIngredients(List<RecipeSuggestionDto> recipeSuggestionDtos) {
        List<RecipeSuggestionDto> toReturn = new LinkedList<>();

        if (recipeSuggestionDtos != null) {
            for (RecipeSuggestionDto recipe : recipeSuggestionDtos) {
                List<RecipeIngredientDto> matchedIngredients = getMatchedIngredients(recipe.extendedIngredients());
                RecipeSuggestionDto toAdd = new RecipeSuggestionDto(
                    recipe.id(),
                    recipe.title(),
                    recipe.servings(),
                    recipe.readyInMinutes(),
                    matchedIngredients,
                    recipe.summary(),
                    recipe.missedIngredients(),
                    recipe.dishTypes()
                );
                toReturn.add(toAdd);
            }
        }
        return toReturn;
    }

    private List<RecipeSuggestionDto> saveUnitsAlsUnits(List<RecipeSuggestionDto> recipes) {

        List<RecipeSuggestionDto> updatedRecipeList = new LinkedList<>();
        for (RecipeSuggestionDto recipeSuggestion : recipes) {
            List<RecipeIngredientDto> updatedIngredients = new LinkedList<>();
            List<RecipeIngredientDto> updatedMissedIngredients = new LinkedList<>();
            for (RecipeIngredientDto recipeIngredient : recipeSuggestion.extendedIngredients()) {
                updatedIngredients.add(this.normalizeIngredient(recipeIngredient));
            }
            if (recipeSuggestion.missedIngredients() != null) {
                for (RecipeIngredientDto recipeIngredient : recipeSuggestion.missedIngredients()) {
                    updatedMissedIngredients.add(this.normalizeIngredient(recipeIngredient));
                }
            }
            updatedRecipeList.add(new RecipeSuggestionDto(recipeSuggestion.id(),
                recipeSuggestion.title(),
                recipeSuggestion.servings(),
                recipeSuggestion.readyInMinutes(),
                updatedIngredients,
                recipeSuggestion.summary(),
                updatedMissedIngredients,
                recipeSuggestion.dishTypes()));
        }
        return updatedRecipeList;
    }

    private RecipeDetailDto saveUnitAlsUnitsForDetail(RecipeDetailDto recipeSuggestion) {
        List<RecipeIngredientDto> updatedIngredients = new LinkedList<>();
        for (RecipeIngredientDto recipeIngredient : recipeSuggestion.extendedIngredients()) {
            updatedIngredients.add(this.normalizeIngredient(recipeIngredient));
        }

        return new RecipeDetailDto(recipeSuggestion.id(),
            recipeSuggestion.title(),
            recipeSuggestion.servings(),
            recipeSuggestion.readyInMinutes(),
            updatedIngredients,
            recipeSuggestion.summary(),
            recipeSuggestion.steps()
        );
    }

    private RecipeSuggestion getRecipeFromApi(Long recipeId) {
        String reqString = "https://api.spoonacular.com/recipes/" + recipeId + "/information" + "?apiKey=" + apiKey + "&includeNutrition=false";
        ResponseEntity<RecipeSuggestion> response = restTemplate.exchange(reqString, HttpMethod.GET, null, new ParameterizedTypeReference<RecipeSuggestion>() {
        });
        RecipeSuggestion recipe = response.getBody();
        List<RecipeIngredientDto> matchedIngredients = getMatchedIngredients(ingredientMapper.entityListToDtoList(response.getBody().getExtendedIngredients()));

        RecipeSuggestionDto toReturn = new RecipeSuggestionDto(recipeId, recipe.getTitle(), recipe.getServings(), recipe.getReadyInMinutes(), matchedIngredients, recipe.getSummary(), null, null);


        return recipeMapper.dtoToEntity(toReturn, ingredientMapper.dtoListToEntityList(matchedIngredients));
    }

    private String getNewReqStringForInformation(Long id) {
        String newReqString = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + id + "/information";
        newReqString += "?includeNutrition=false";
        return newReqString;
    }

    // The number of recipes can be adjusted here.
    private String getRequestStringForRecipeSearch(List<ItemDto> items) throws
        DeepLException, InterruptedException {
        translator = new Translator(translateKey);
        List<String> ingredients = new LinkedList<>();
        for (ItemDto item : items) {
            ingredients.add(item.productName());
        }
        ingredients = reorderList(ingredients);
        String requestString = apiUrlNew + "?";

        boolean isFirst = true;
        int max = 0;
        for (String ingredient : ingredients) {
            if (max == 10) {
                break;
            }
            TextResult textResult = translator.translateText(ingredient, null, "en-GB");
            ingredient = textResult.getText();
            if (isFirst) {
                requestString += "ingredients=" + ingredient;
                isFirst = false;
            } else {
                requestString += "%2C" + ingredient;
            }
            max++;
        }
        requestString += "&number=9";
        return requestString + "&ranking=2";
    }

    private Unit getMinUnit(Unit unit) {
        if (unit == null) {
            return null;
        }
        if (unit.getSubUnit().isEmpty()) {
            return unit;
        }
        for (Unit subUnit : unit.getSubUnit()) {
            return subUnit;
        }

        return null;
    }

    private List<String> reorderList(List<String> ingredientList) {

        List<String> shuffledList = new ArrayList<>(ingredientList);

        Collections.shuffle(shuffledList);

        return shuffledList;
    }

    private Double getItemQuantityTotalInMinQuantity(List<DigitalStorageItem> digitalStorageItems, RecipeIngredientDto ingredient) throws
        ValidationException, ConflictException {

        Double toRet = 0.0;

        Unit test = getMinUnit(unitMapper.unitDtoToEntity(ingredient.unitEnum()));
        for (DigitalStorageItem digitalStorageItem : digitalStorageItems) {
            if (digitalStorageItem.getItemCache().getProductName().equals(ingredient.name())) {
                if (digitalStorageItem.getItemCache().getUnit().equals(test)) {
                    toRet += digitalStorageItem.getQuantityCurrent();
                } else if (getMinUnit(digitalStorageItem.getItemCache().getUnit()).equals(test)) {
                    Double convert = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), test, digitalStorageItem.getQuantityCurrent());
                    toRet += convert;
                }
            }
        }
        return toRet;
    }

    private List<DigitalStorageItem> minimizeUnits(List<DigitalStorageItem> digitalStorageItems) {

        List<DigitalStorageItem> minimizedDigitalStorageItems = new LinkedList<>();


        for (DigitalStorageItem digitalStorageItem : digitalStorageItems) {
            Unit minUnit = getMinUnit(digitalStorageItem.getItemCache().getUnit());

            DigitalStorageItem minimizedDigitalStorageItem = null;

            if (digitalStorageItem.getClass().equals(DigitalStorageItem.class)) {
                minimizedDigitalStorageItem = new DigitalStorageItem();
            } else if (digitalStorageItem.getClass().equals(AlwaysInStockDigitalStorageItem.class)) {
                minimizedDigitalStorageItem = new AlwaysInStockDigitalStorageItem();
                minimizedDigitalStorageItem.setMinimumQuantity(digitalStorageItem.getMinimumQuantity());
            } else {
                throw new FatalException("minimizedDigitalStorageItemsFatal");
            }
            double convertedQuantity = unitService.convertUnits(digitalStorageItem.getItemCache().getUnit(), minUnit, digitalStorageItem.getQuantityCurrent());
            minimizedDigitalStorageItem.setItemId(digitalStorageItem.getItemId());
            minimizedDigitalStorageItem.getItemCache().setUnit(minUnit);
            minimizedDigitalStorageItem.setQuantityCurrent(convertedQuantity);
            minimizedDigitalStorageItem.getItemCache().setEan(digitalStorageItem.getItemCache().getEan());
            minimizedDigitalStorageItem.getItemCache().setGeneralName(digitalStorageItem.getItemCache().getGeneralName());
            minimizedDigitalStorageItem.getItemCache().setProductName(digitalStorageItem.getItemCache().getProductName());
            minimizedDigitalStorageItem.getItemCache().setBrand(digitalStorageItem.getItemCache().getBrand());
            minimizedDigitalStorageItem.getItemCache().setQuantityTotal(digitalStorageItem.getItemCache().getQuantityTotal());
            minimizedDigitalStorageItem.setExpireDate(digitalStorageItem.getExpireDate());
            minimizedDigitalStorageItem.getItemCache().setDescription(digitalStorageItem.getItemCache().getDescription());
            minimizedDigitalStorageItem.setPriceInCent(digitalStorageItem.getPriceInCent());
            minimizedDigitalStorageItem.setBoughtAt(digitalStorageItem.getBoughtAt());
            minimizedDigitalStorageItem.setDigitalStorage(digitalStorageItem.getDigitalStorage());
            minimizedDigitalStorageItem.setIngredientList(digitalStorageItem.getIngredientList());
            minimizedDigitalStorageItem.getItemCache().setAlternativeNames(digitalStorageItem.getItemCache().getAlternativeNames());


            minimizedDigitalStorageItems.add(minimizedDigitalStorageItem);


        }

        return minimizedDigitalStorageItems;

    }

    private Long getCookbookIdForUser() throws AuthorizationException {
        List<Cookbook> cookbookList = findAllCookbooks();
        Cookbook matchingCookbook = null;
        if (!cookbookList.isEmpty()) {
            matchingCookbook = cookbookList.stream().toList().get(0);
        }
        if (matchingCookbook != null) {
            List<Long> allowedUsers = authService.getUserFromToken().getSharedFlat().getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();


            authorization.authorizeUser(
                allowedUsers,
                "The given cookbook does not belong to the user's shared flat!"
            );
            return matchingCookbook.getId();
        } else {
            return null;
        }
    }

    private Long getStorageIdForUser() throws AuthorizationException {
        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        DigitalStorage matchingDigitalStorage = null;
        if (!digitalStorageList.isEmpty()) {
            matchingDigitalStorage = digitalStorageList.stream().toList().get(0);
        }
        if (matchingDigitalStorage != null) {
            List<Long> allowedUsers = authService.getUserFromToken().getSharedFlat().getUsers().stream()
                .map(ApplicationUser::getId)
                .toList();
            authorization.authorizeUser(
                allowedUsers,
                "The given cookbook does not belong to the user's shared flat!"
            );

            return matchingDigitalStorage.getStorageId();
        } else {
            return null;
        }
    }

    private RecipeIngredientDto normalizeIngredient(RecipeIngredientDto ingredient) {
        String unitName = ingredient.unit();
        double amount = ingredient.amount();

        Unit unit;
        if (unitName.isEmpty()) {
            unit = unitService.findByName("pcs");
        } else {
            try {
                unit = unitService.findByName(unitName);
            } catch (NotFoundException notFoundException) {
                unit = unitService.findByName("pcs");
            }

        }
        Unit minUnit = null;
        double convertedAmount = 0;
        if (!unit.getName().equals("tbsp") && !unit.getName().equals("tsp")) {
            minUnit = getMinUnit(unit);
            convertedAmount = unitService.convertUnits(unit, minUnit, amount);
        } else {
            minUnit = unit;
            convertedAmount = ingredient.amount();
        }

        convertedAmount = truncateToDecimalPlaces(convertedAmount, 2);

        return new RecipeIngredientDto(
            ingredient.id(),
            ingredient.name(),
            unitMapper.entityToUnitDto(minUnit).name(),
            unitMapper.entityToUnitDto(minUnit),
            convertedAmount,
            ingredient.matched(),
            ingredient.autoMatched(),
            ingredient.haveWithDifferentUnits(),
            ingredient.realName(),
            ingredient.matchedItem()
        );
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "ae23e73d96msh9381960a9bfdf3dp189734jsne7387514766a");
        headers.set("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        return httpEntity;
    }

    private double truncateToDecimalPlaces(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
        return bd.doubleValue();

    }
}

