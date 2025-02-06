package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ShoppingRepositoryTest implements TestData {

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;
    @Autowired
    private ShoppingListRepository shoppingListRepository;
    @Autowired
    private SharedFlatRepository sharedFlatRepository;

    @Test
    @DisplayName("Positive test for saving a valid shopping item")
    @Disabled
    public void saveValidShoppingItemThenFindItByProductName() {
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.getItemCache().setProductName("Product1");
        ShoppingItem savedItem = shoppingItemRepository.save(shoppingItem);

        ShoppingItem foundItem = shoppingItemRepository.findFirstByItemCacheProductName("Product1");

        assertAll(
            () -> assertEquals(savedItem.getItemId(), foundItem.getItemId()),
            () -> assertEquals(savedItem.getItemCache().getProductName(), foundItem.getItemCache().getProductName())
        );
    }

    @Test
    @DisplayName("Positive test for deleting an existing shopping item")
    public void deleteExistingShoppingItemAndCheckIfSuccessfullyDeleted() {
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.getItemCache().setProductName("Product2");
        ShoppingItem savedItem = shoppingItemRepository.save(shoppingItem);

        ShoppingItem foundItem = shoppingItemRepository.findFirstByItemCacheProductName("Product2");
        assertNotNull(foundItem);

        shoppingItemRepository.delete(savedItem);

        ShoppingItem deletedItem = shoppingItemRepository.findFirstByItemCacheProductName("Product2");
        assertNull(deletedItem, "Deleted item should not be found");
    }

    @Test
    @DisplayName("Test searching items by shopping list and product name")
    public void testSearchItemsByShoppingListAndProductName() {
        SharedFlat sharedFlat = new SharedFlat().setName("Happy House");
        SharedFlat saved = sharedFlatRepository.save(sharedFlat);

        String shoppingListName = "Groceries";
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(shoppingListName);
        shoppingList.setSharedFlat(saved);
        ShoppingList savedList = shoppingListRepository.save(shoppingList);

        ShoppingItem item1 = new ShoppingItem();
        item1.getItemCache().setProductName("Apple");
        item1.setShoppingList(savedList);
        shoppingItemRepository.save(item1);

        ShoppingItem item2 = new ShoppingItem();
        item2.getItemCache().setProductName("Banana");
        item2.setShoppingList(savedList);
        shoppingItemRepository.save(item2);

        List<ShoppingItem> items = shoppingItemRepository.searchItemsByShoppingListNameAndShoppingListSharedFlatIdAndItemCacheProductName(shoppingListName, sharedFlat.getId(), "Banana");

        assertAll(
            () -> assertEquals(1, items.size()),
            () -> assertEquals("Banana", items.get(0).getItemCache().getProductName())
        );
    }


}
