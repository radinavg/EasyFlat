package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String unit;

    private double amount;

    private String realName;

    @ManyToOne
    private RecipeSuggestion recipeSuggestion;

    @ManyToOne
    @JoinColumn(name = "unitEnum_id", nullable = true)
    private Unit unitEnum;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeSuggestion getRecipeSuggestion() {
        return recipeSuggestion;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public void setRecipeSuggestion(RecipeSuggestion recipeSuggestion) {
        this.recipeSuggestion = recipeSuggestion;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Unit getUnitEnum() {
        return unitEnum;
    }

    public void setUnitEnum(Unit unitEnum) {
        this.unitEnum = unitEnum;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipeIngredient that = (RecipeIngredient) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(name, that.name) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unit, amount);
    }


}
