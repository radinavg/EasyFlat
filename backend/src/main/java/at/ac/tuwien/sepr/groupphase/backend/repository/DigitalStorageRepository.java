package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalStorageRepository extends JpaRepository<DigitalStorage, Long> {

    List<DigitalStorage> findByTitleContainingAndSharedFlatIs(String title, SharedFlat sharedFlat);

    @Query("SELECT i FROM DigitalStorageItem i WHERE i.digitalStorage.storageId = :storageId AND "
        + "(:title IS NULL OR LOWER(i.itemCache.generalName) LIKE LOWER(CONCAT('%', :title, '%'))) AND "
        + "(:fillLevel IS NULL OR "
        + "(:fillLevel = 'full' AND ((cast(i.quantityCurrent as float ))/(cast(i.itemCache.quantityTotal as float ))) >= 0.4) OR "
        + "(:fillLevel = 'nearly_empty' AND ((cast(i.quantityCurrent as float ))/(cast(i.itemCache.quantityTotal as float ))) > 0.2 AND ((cast(i.quantityCurrent as float ))/(cast(i.itemCache.quantityTotal as float ))) < 0.4) OR "
        + "(:fillLevel = 'empty' AND ((cast(i.quantityCurrent as float ))/(cast(i.itemCache.quantityTotal as float ))) < 0.2)) AND "
        + "(:alwaysInStock IS NULL OR TYPE(i) = :alwaysInStock) ")
    List<DigitalStorageItem> searchItems(@Param("storageId") Long storageId,
                                         @Param("title") String title,
                                         @Param("fillLevel") String fillLevel,
                                         @Param("alwaysInStock") Class alwaysInStock);

    List<DigitalStorage> findBySharedFlatIs(SharedFlat sharedFlat);
}



