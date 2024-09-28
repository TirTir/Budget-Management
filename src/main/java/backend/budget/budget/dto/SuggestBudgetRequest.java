package backend.budget.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuggestBudgetRequest {
    private Long totalBudget;
}
