package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebitRepository extends JpaRepository<Debit, Long> {

    List<Debit> findAllById_ExpenseIsIn(List<Expense> expenses);
}
