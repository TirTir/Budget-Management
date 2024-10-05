package backend.budget.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
public class GuideExpenseResponse {
    private long totalAmount; // 총액
    private Map<String, CategoryExpenseDetail> categoryDetails;

    @Data
    @AllArgsConstructor
    public static class CategoryExpenseDetail {
        private long idealAmount; // 적정 금액
        private long spentAmount; // 지출한 금액
        private double riskPercentage; // 위험도 (퍼센트로 표시)
    }
}