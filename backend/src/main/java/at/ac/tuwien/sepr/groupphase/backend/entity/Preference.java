package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Objects;

@Entity(name = "preference") // name of the table
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Chore firstId;
    @ManyToOne
    private Chore secondId;
    @ManyToOne
    private Chore thirdId;
    @ManyToOne
    private Chore fourthId;
    @OneToOne
    private ApplicationUser user;

    public Long getId() {
        return id;
    }


    public Chore getFirst() {
        return firstId;
    }

    public Chore getSecond() {
        return secondId;
    }

    public Chore getThird() {
        return thirdId;
    }

    public Chore getFourth() {
        return fourthId;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUserId(ApplicationUser user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirst(Chore firstId) {
        this.firstId = firstId;
    }

    public void setSecond(Chore secondId) {
        this.secondId = secondId;
    }

    public void setThird(Chore thirdId) {
        this.thirdId = thirdId;
    }

    public void setFourth(Chore fourthId) {
        this.fourthId = fourthId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Preference that = (Preference) o;
        return Objects.equals(id, that.id) && Objects.equals(firstId, that.firstId)
            && Objects.equals(secondId, that.secondId)
            && Objects.equals(thirdId, that.thirdId)
            && Objects.equals(fourthId, that.fourthId)
            && Objects.equals(user, that.user);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, firstId, secondId, thirdId, fourthId, user);
    }
}
