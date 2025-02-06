package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface ShoppingListService {

    /**
     * Validates and Creates a new {@link ShoppingItem} in the db.
     *
     * @param itemDto a shopping item without ID
     * @return an object of type {@link ShoppingItem} which is persisted and has an ID
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    ShoppingItem createShoppingItem(ShoppingItemDto itemDto) throws ValidationException, ConflictException, AuthorizationException;

    /**
     * Search for a shopping item in the database with given ID.
     *
     * @param itemId a valid ID
     * @return if the id exists in the DB, an Optional of a persisted ShoppingItem with given ID, an empty Optional otherwise
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    Optional<ShoppingItem> getShoppingItemById(Long itemId) throws AuthorizationException;

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param name a valid listName
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     */
    Optional<ShoppingList> getShoppingListByName(String name);

    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param id a valid ID of a ShoppingList
     * @return if the id exists in the DB, an Optional of a persisted ShoppingList with given ID, an empty Optional otherwise
     */
    Optional<ShoppingList> getShoppingListById(Long id);


    /**
     * Search for a shopping list in the database with given ID.
     *
     * @param id a valid ID of a ShoppingList
     * @param itemSearchDto search parameters consisting of the product's name and its label's value
     * @return if the id exists in the DB, a List of a persisted ShoppingItems with the given ID, an empty Optional otherwise
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    List<ShoppingItem> getItemsByShoppingListId(Long id, ShoppingItemSearchDto itemSearchDto) throws AuthorizationException;

    /**
     * Create a new ShoppingList in the db.
     *
     * @param shoppingListDto a DTO of type shopping list ID null
     * @return an object of type {@link ShoppingList} which is persisted and has an ID
     */
    ShoppingList createList(ShoppingListDto shoppingListDto) throws ValidationException, ConflictException;

    /**
     * Delete a ShoppingItem from the db based on its ID.
     *
     * @param itemId a valid ID of a ShoppingItem
     * @return the deleted ShoppingItem
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    ShoppingItem deleteItem(Long itemId) throws AuthorizationException, ConflictException;

    /**
     * Delete a ShoppingList from the db based on its ID.
     *
     * @param shopId a valid ID of a ShoppingList
     * @return the deleted ShoppingList
     */
    ShoppingList deleteList(Long shopId) throws ValidationException, AuthorizationException, ConflictException;

    /**
     * Get all ShoppingLists from the db filtered by search parameters.
     *
     * @param searchParams name of the list, through which we search for it. Can also be null
     * @return a List of all persisted ShoppingLists
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    List<ShoppingList> getShoppingLists(String searchParams) throws AuthorizationException;

    /**
     * Transfer ShoppingItems to the server.
     *
     * @param items a List of ShoppingItemDto to be transferred
     * @return a List of DigitalStorageItem objects
     */
    List<DigitalStorageItem> transferToServer(List<ShoppingItemDto> items) throws AuthorizationException, ValidationException, ConflictException;

    /**
     * Validates and Updates a new {@link ShoppingItem} in the db.
     *
     * @param shoppingItemDto a DTO of type shopping item with existing ID
     * @return an object of type {@link ShoppingItem} which is updated
     * @throws AuthorizationException if the user is not authorized to access a resource
     * @throws ConflictException if there is a conflict with the persisted data
     * @throws ValidationException if the data in shoppingItemDto is not valid
     */
    ShoppingItem updateShoppingItem(ShoppingItemDto shoppingItemDto)
        throws ConflictException, ValidationException, AuthorizationException;

    /**
     * Deletes the specified chores.
     *
     * @param itemIds List of chore IDs to be deleted.
     * @return List of remaining chores after deletion.
     */
    List<ShoppingItem> deleteShoppingItems(List<Long> itemIds) throws AuthorizationException, ConflictException;
}

