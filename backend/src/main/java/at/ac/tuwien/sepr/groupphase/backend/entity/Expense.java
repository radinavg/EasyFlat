package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private SplitBy splitBy;

    @Column
    private Double amountInCents;

    @Column
    private LocalDateTime createdAt;

    @Column
    private Integer periodInDays;

    @Column
    private Boolean addedViaStorage;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ApplicationUser paidBy;

    @OneToMany(mappedBy = "id.expense", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Debit> debitUsers = new ArrayList<>();

    @ManyToOne
    private SharedFlat sharedFlat;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SplitBy getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(SplitBy splitBy) {
        this.splitBy = splitBy;
    }

    public Double getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Double amountInCents) {
        this.amountInCents = amountInCents;
    }

    public ApplicationUser getPaidBy() {
        return paidBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SharedFlat getSharedFlat() {
        return sharedFlat;
    }

    public void setSharedFlat(SharedFlat sharedFlat) {
        this.sharedFlat = sharedFlat;
    }

    public void setPaidBy(ApplicationUser paidBy) {
        this.paidBy = paidBy;
        paidBy.getMyExpense().add(this);
    }

    public List<Debit> getDebitUsers() {
        return debitUsers;
    }

    public void setDebitUsers(List<Debit> debitUsers) {
        this.debitUsers = debitUsers;
    }

    public Integer getPeriodInDays() {
        return periodInDays;
    }

    public void setPeriodInDays(Integer periodInDays) {
        this.periodInDays = periodInDays;
    }

    public Boolean getAddedViaStorage() {
        return addedViaStorage;
    }

    public void setAddedViaStorage(Boolean addedViaStorage) {
        this.addedViaStorage = addedViaStorage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
