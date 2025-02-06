package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    /**
     * Finds a ShoppingList by name and its associated SharedFlat.
     *
     * @param name       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return The found ShoppingList if it exists for the given name and SharedFlat.
     */
    ShoppingList findByNameAndSharedFlatIs(String name, SharedFlat sharedFlat);

    /**
     * Finds a ShoppingList by name and its associated SharedFlat.
     *
     * @param shopId       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return The found ShoppingList if it exists for the given name and SharedFlat.
     */
    @Query("SELECT sl FROM ShoppingList sl WHERE sl.id = :shopId AND sl.sharedFlat = :sharedFlat")
    ShoppingList findByIdAndSharedFlatIs(@Param("shopId") Long shopId, @Param("sharedFlat") SharedFlat sharedFlat);

    /**
     * Retrieves a ShoppingList by name and its associated SharedFlat.
     *
     * @param name       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return An Optional containing the found ShoppingList, if present.
     */
    Optional<ShoppingList> getByNameAndSharedFlatIs(String name, SharedFlat sharedFlat);

    /**
     * Retrieves a ShoppingList by its shopListId and its associated SharedFlat.
     *
     * @param id         The ID of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return An Optional containing the found ShoppingList, if present.
     */
    Optional<ShoppingList> getByIdAndSharedFlatIs(Long id, SharedFlat sharedFlat);

    /**
     * Searches for ShoppingLists based on ShoppingList name.
     *
     * @param name       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return A list of ShoppingLists based on the criteria.
     */
    List<ShoppingList> findAllByNameContainingIgnoreCaseAndSharedFlatIs(String name, SharedFlat sharedFlat);

    /**
     * Deletes a ShoppingList by its ID.
     *
     * @param listId The ID of the ShoppingList to be deleted.
     */
    @Query("DELETE FROM ShoppingList sl WHERE sl.id = :listId")
    void deleteByListId(@Param("listId") Long listId);

    /**
     * Retrieves all ShoppingLists by their associated SharedFlat.
     *
     * @param sharedFlat The SharedFlat entity associated with the ShoppingLists.
     * @return A List containing the found ShoppingLists, if present.
     */
    List<ShoppingList> findBySharedFlat(SharedFlat sharedFlat);
}
