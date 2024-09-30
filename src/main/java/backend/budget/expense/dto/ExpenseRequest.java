package backend.budget.expense.dto;

import lombok.Data;

@Data
public class ExpenseRequest {
    private Long categoryId;
    private Long minAmount;
    private Long maxAmount;
}
