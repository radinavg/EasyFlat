package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class DigitalStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storageId;

    @Column
    private String title;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SharedFlat sharedFlat;

    @OneToMany(mappedBy = "digitalStorage", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<DigitalStorageItem> digitalStorageItemList = new ArrayList<>();


    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long id) {
        this.storageId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DigitalStorageItem> getItemList() {
        return digitalStorageItemList;
    }

    public void setItemList(List<DigitalStorageItem> digitalStorageItemList) {
        this.digitalStorageItemList = digitalStorageItemList;
    }

    @JsonBackReference
    public SharedFlat getSharedFlat() {
        return sharedFlat;
    }

    public void setSharedFlat(SharedFlat sharedFlat) {
        this.sharedFlat = sharedFlat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DigitalStorage digitalStorage = (DigitalStorage) o;
        return Objects.equals(storageId, digitalStorage.storageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageId);
    }
}
