package backend.budget.budget.repository;

import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByUserAndCategory(User user, Category category);
}
