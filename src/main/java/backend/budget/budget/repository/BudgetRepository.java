package backend.budget.budget.repository;

import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByUserAndCategory(User user, Category category);

    @Query("SELECT b.category.name, AVG(b.amount) as avgAmount FROM Budget b GROUP BY b.category.name")
    List<Object[]> findAverageBudgetByCategory();

    // 해당 월별 모든 카테고리 예산
    List<Budget> findByUserIdAndPeriod(Long userId, LocalDate period);
}
