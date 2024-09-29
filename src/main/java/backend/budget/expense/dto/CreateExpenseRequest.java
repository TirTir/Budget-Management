package backend.budget.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateExpenseRequest {
    private Long amount;
    private LocalDate expenseDate;
    private String memo;
    private Long categoryId;
}
