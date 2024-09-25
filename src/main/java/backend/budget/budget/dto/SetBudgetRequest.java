package backend.budget.budget.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SetBudgetRequest {
    private Long category;
    private Long amount;
    private LocalDate period;
}
