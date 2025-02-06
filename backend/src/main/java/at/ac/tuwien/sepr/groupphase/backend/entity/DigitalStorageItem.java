package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@GroupSequence({DigitalStorageItem.class, ShoppingItem.class}) // Define the order of group validation
public class DigitalStorageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column
    private Double quantityCurrent;

    @Column
    @FutureOrPresent(message = "You cannot store products which are over the expire date")
    private LocalDate expireDate;

    @Column
    private Long priceInCent;

    @Column
    private String boughtAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private ItemCache itemCache = new ItemCache();

    @ManyToOne
    @NotNull(message = "A DigitalStorageItem need to be linked to a storage")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DigitalStorage digitalStorage;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Ingredient> ingredientList;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long id) {
        this.itemId = id;
    }

    public Double getQuantityCurrent() {
        return quantityCurrent;
    }

    public void setQuantityCurrent(Double quantityCurrent) {
        this.quantityCurrent = quantityCurrent;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public Long getPriceInCent() {
        return priceInCent;
    }

    public void setPriceInCent(Long priceInCent) {
        this.priceInCent = priceInCent;
    }

    public String getBoughtAt() {
        return boughtAt;
    }

    public void setBoughtAt(String boughtAt) {
        this.boughtAt = boughtAt;
    }

    public ItemCache getItemCache() {
        return itemCache;
    }

    public void setItemCache(ItemCache itemCache) {
        this.itemCache = itemCache;
    }

    public DigitalStorage getDigitalStorage() {
        return digitalStorage;
    }

    public void setDigitalStorage(DigitalStorage digitalStorage) {
        this.digitalStorage = digitalStorage;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public Long getMinimumQuantity() {
        return null;
    }

    public void setMinimumQuantity(Long minimumQuantity) {

    }

    public boolean alwaysInStock() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DigitalStorageItem digitalStorageItem = (DigitalStorageItem) o;
        return Objects.equals(itemId, digitalStorageItem.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }


}
