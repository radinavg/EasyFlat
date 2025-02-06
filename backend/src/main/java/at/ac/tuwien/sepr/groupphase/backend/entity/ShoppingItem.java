package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Objects;

@Entity
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column
    private Double quantityCurrent;

    @Column
    private Boolean alwaysInStock;

    @Column
    private Double minimumQuantity;

    @Column
    private Long priceInCent;

    @Column
    private String boughtAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private ItemCache itemCache = new ItemCache();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ItemLabel> labels;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ShoppingList shoppingList;

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public ShoppingItem setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
        return this;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long id) {
        this.itemId = id;
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

    public Double getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Double minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public boolean alwaysInStock() {
        return Objects.requireNonNullElse(this.alwaysInStock, false);
    }

    public Double getQuantityCurrent() {
        return quantityCurrent;
    }

    public void setQuantityCurrent(Double quantityCurrent) {
        this.quantityCurrent = quantityCurrent;
    }

    public Boolean getAlwaysInStock() {
        return alwaysInStock;
    }

    public void setAlwaysInStock(Boolean alwaysInStock) {
        this.alwaysInStock = alwaysInStock;
    }

    public ItemCache getItemCache() {
        return itemCache;
    }

    public void setItemCache(ItemCache itemCache) {
        this.itemCache = itemCache;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    public ShoppingItem setLabels(List<ItemLabel> labels) {
        this.labels = labels;
        return this;
    }

    public List<ItemLabel> getLabels() {
        return labels;
    }
}
