package backend.budget.budget.dto;

import lombok.Data;

@Data
public class BudgetResponse {
    private String category;
    private Long amount;
}
