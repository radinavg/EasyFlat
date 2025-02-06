package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<DigitalStorageItem, Long> {

    List<DigitalStorageItem> findAllByItemCache_GeneralNameContainingIgnoreCaseOrItemCache_BrandContainingIgnoreCaseOrBoughtAtContainingIgnoreCase(
        String generalName,
        String brand,
        String boughtAt);

    List<DigitalStorageItem> findAllByDigitalStorage_StorageIdIsAndItemCache_GeneralNameContainsIgnoreCase(
        Long storageId,
        String generalName
    );

    List<DigitalStorageItem> findAllByDigitalStorage_StorageId(
        Long storageId
    );

    List<DigitalStorageItem> findAllByDigitalStorage_StorageIdAndItemCache_ProductNameStartingWithIgnoreCase(Long storageId, String productName);

    @Query("SELECT a FROM DigitalStorageItem i JOIN i.itemCache.alternativeNames a WHERE i.itemId = :itemId")
    List<AlternativeName> findAlternativeNamesByItemId(@Param("itemId") Long itemId);

}
