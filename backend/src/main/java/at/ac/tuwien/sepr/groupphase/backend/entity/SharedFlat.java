package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity(name = "shared_flat")
public class SharedFlat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String password;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sharedFlat")
    private Set<ApplicationUser> users = new HashSet<>();
    @OneToOne(mappedBy = "sharedFlat", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private DigitalStorage digitalStorage;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "sharedFlat")
    private List<ShoppingList> shoppingLists;
    @OneToOne(mappedBy = "sharedFlat", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Cookbook cookbook;

    public SharedFlat() {
    }

    public SharedFlat setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SharedFlat setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    @JsonManagedReference
    public DigitalStorage getDigitalStorage() {
        return digitalStorage;
    }

    public void setDigitalStorage(DigitalStorage digitalStorage) {
        this.digitalStorage = digitalStorage;
        if (digitalStorage != null) {
            digitalStorage.setSharedFlat(this);
        }
    }

    @JsonManagedReference
    public Cookbook getCookbook() {
        return cookbook;
    }

    public void setCookbook(Cookbook cookbook) {
        this.cookbook = cookbook;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public Set<ApplicationUser> getUsers() {
        return users;
    }

    public void setUsers(Set<ApplicationUser> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SharedFlat that = (SharedFlat) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

