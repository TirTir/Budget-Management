package backend.budget.budget.repository;

import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByUserAndCategory(User user, Category category);

    @Query("SELECT b.category.name, AVG(b.amount) as avgAmount FROM Budget b GROUP BY b.category.name")
    List<Object[]> findAverageBudgetByCategory();
}
