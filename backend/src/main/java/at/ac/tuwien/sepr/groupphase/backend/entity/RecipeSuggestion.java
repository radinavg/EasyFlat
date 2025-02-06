package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RecipeSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column
    private String title;
    @Column
    private Integer servings;
    @Column
    private Integer readyInMinutes;

    @Version
    Integer version;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<RecipeIngredient> extendedIngredients;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<RecipeIngredient> missingIngredients;
    @Column(columnDefinition = "TEXT")
    private String summary;

    @ManyToOne
    //@NotNull(message = "A Item need to be linked to a cookbook")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cookbook cookbook;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setExtendedIngredients(List<RecipeIngredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public Integer getVersion() {
        return version;
    }

    public List<RecipeIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<RecipeIngredient> getMissingIngredients() {
        return missingIngredients;
    }

    public void setMissingIngredients(List<RecipeIngredient> missingIngredients) {
        this.missingIngredients = missingIngredients;
    }

    public Cookbook getCookbook() {
        return cookbook;
    }

    public void setCookbook(Cookbook cookbook) {
        this.cookbook = cookbook;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipeSuggestion that = (RecipeSuggestion) o;
        for (int i = 0; i < extendedIngredients.size(); i++) {
            if (!extendedIngredients.isEmpty() && !that.extendedIngredients.isEmpty()) {
                if (!extendedIngredients.get(i).equals(that.extendedIngredients.get(i))) {
                    return false;
                }
            }
        }

        return Objects.equals(title, that.title) && Objects.equals(servings, that.servings) && Objects.equals(readyInMinutes, that.readyInMinutes) && Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, servings, readyInMinutes, extendedIngredients, summary, cookbook);
    }

}
