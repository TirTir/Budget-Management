package backend.budget.expense.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data @Builder
public class DetailExpenseResponse {
    private Long amount;
    private LocalDate expenseDate;
    private String memo;
    private String categoryName;
}
