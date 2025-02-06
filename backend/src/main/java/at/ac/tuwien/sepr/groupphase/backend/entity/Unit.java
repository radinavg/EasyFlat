package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Unit {

    public Unit() {
    }

    @Id
    private String name;

    @Nullable
    private Long convertFactor;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Unit> subUnit = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String unit) {
        this.name = unit;
    }

    @Nullable
    public Long getConvertFactor() {
        return convertFactor;
    }

    public void setConvertFactor(@Nullable Long convertFactor) {
        this.convertFactor = convertFactor;
    }

    public Set<Unit> getSubUnit() {
        return subUnit;
    }

    public void setSubUnit(Set<Unit> subUnit) {
        this.subUnit = subUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit unit1 = (Unit) o;
        return Objects.equals(name, unit1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Unit{"
            + "unit='" + name + '\''
            + '}';
    }
}
