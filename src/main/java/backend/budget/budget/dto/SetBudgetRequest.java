package backend.budget.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SetBudgetRequest {
    private Long category;
    private Long amount;
    private LocalDate period;
}
