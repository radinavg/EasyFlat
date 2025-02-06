package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AlwaysInStock")
public class AlwaysInStockDigitalStorageItem extends DigitalStorageItem {

    @Column
    private Long minimumQuantity;

    public Long getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Long minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public boolean alwaysInStock() {
        return true;
    }
}
