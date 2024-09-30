package backend.budget.expense.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data @Builder
public class ExpenseResponse {
    private List<DetailExpenseResponse> expenses;
    private Long totalExpense; // 지출 합계
    private Map<String, Long> categoryExpense;// 카테고리 별 지출 합계
}
