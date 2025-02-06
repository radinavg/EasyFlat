package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByPaidByIs(ApplicationUser user);

    @Query("SELECT e FROM Expense e "
        + "JOIN application_user user ON e.paidBy.id = user.id "
        + "WHERE user.sharedFlat.id = :flatId "
        + "AND (:title IS NULL OR UPPER(e.title) LIKE UPPER(CONCAT('%', :title, '%')))"
        + "AND (:paidById IS NULL OR e.paidBy.id = :paidById)"
        + "AND (:minAmountInCents IS NULL OR e.amountInCents >= :minAmountInCents)"
        + "AND (:maxAmountInCents IS NULL OR e.amountInCents <= :maxAmountInCents)"
        + "AND (:fromCreatedAt IS NULL OR e.createdAt >= :fromCreatedAt)"
        + "AND (:toCreatedAt IS NULL OR e.createdAt <= :toCreatedAt)"
        + "ORDER BY e.createdAt DESC")

    List<Expense> findByCriteria(@Param("flatId") Long flatId,
                                 @Param("title") String title,
                                 @Param("paidById") Long paidById,
                                 @Param("minAmountInCents") Double minAmountInCents,
                                 @Param("maxAmountInCents") Double maxAmountInCents,
                                 @Param("fromCreatedAt") LocalDateTime fromCreatedAt,
                                 @Param("toCreatedAt") LocalDateTime toCreatedAt
    );

    List<Expense> findByPaidByIsIn(Set<ApplicationUser> users);

    List<Expense> findAllByPeriodInDaysIsNotNull();
}

