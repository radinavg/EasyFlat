package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service for working with Storages.
 */
public interface DigitalStorageService {

    /**
     * Search for a storage in the database with given ID.
     *
     * @param id a valid ID
     * @return if the id exists in the DB, an Optional of a persisted DigitalStorage with given ID, an empty optional otherwise
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    DigitalStorage findById(Long id) throws AuthorizationException;

    /**
     * Search for all Storages stored in the database which matches with the given search criteria.
     *
     * @param digitalStorageSearchDto search criteria
     * @return a List of all persisted Storages
     * @throws AuthorizationException if the user is not authorized to access a resource
     */
    List<DigitalStorage> findAll(DigitalStorageSearchDto digitalStorageSearchDto) throws AuthorizationException;

    /**
     * Search for all Items of a DigitalStorage stored in the database filtered by search parameters.
     *
     * @param itemSearchDto search parameters
     * @return a List of filtered items
     * @throws ValidationException if alwaysInStock is null
     */
    List<ItemListDto> searchItems(ItemSearchDto itemSearchDto) throws ValidationException;

    /**
     * Validates and Creates a new {@link DigitalStorage} in the db.
     *
     * @param storageDto a storage without ID
     * @return an object of type {@link DigitalStorage} which is persisted and has an ID
     * @throws AuthorizationException if the user is not authorized to access a resource
     * @throws ValidationException if the given storageDto contains invalid values
     * @throws ConflictException if the given storageDto has an ID
     */
    DigitalStorage create(DigitalStorageDto storageDto) throws AuthorizationException, ValidationException, ConflictException;

    /**
     * Gets an item from digital storage and adds it to the main shopping list.
     *
     * @param itemDto existing ID of a storage
     * @return the added item of type {@link ShoppingItem}
     * @throws AuthorizationException if the user is not authorized to access a resource
     * @throws ValidationException if the data in itemDto is not valid
     */
    ShoppingItem addItemToShopping(ItemDto itemDto) throws AuthorizationException, ValidationException;
}
