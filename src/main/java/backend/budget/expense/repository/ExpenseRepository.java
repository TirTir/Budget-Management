package backend.budget.expense.repository;

import backend.budget.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

    // 카테고리의 지정된 날짜 범위 내 지출한 금액의 합계
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId AND e.expenseDate BETWEEN :startDate AND :endDate")
    Long getTotalExpenseByUserIdAndCategoryIdAndDateBetween(@Param("userId") Long userId,
                                                            @Param("categoryId") Long categoryId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);
}
