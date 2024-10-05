package backend.budget.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SuggestExpenseResponse {
    private long totalAmount; // 총액
    private Map<String, Long> categoryAmount; // 카테고리 별 금액
    private String message;
}
