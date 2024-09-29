package backend.budget.expense.entity;

import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    private String memo;

    private boolean excludedSum; // 합계 제외 여부

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public void updateExpense(Long amount, LocalDate expenseDate, Category category, String memo) {
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.category = category;
        this.memo = memo;
    }

    public void setExcludedSum(){
        this.excludedSum = !this.excludedSum;
    }
}
