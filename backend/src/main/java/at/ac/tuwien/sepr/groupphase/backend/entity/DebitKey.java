package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DebitKey implements Serializable {

    @ManyToOne
    private Expense expense;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ApplicationUser user;

    public DebitKey() {
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expenseId) {
        this.expense = expenseId;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser userId) {
        this.user = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DebitKey debitKey = (DebitKey) o;
        return Objects.equals(expense, debitKey.expense) && Objects.equals(user, debitKey.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expense, user);
    }
}
