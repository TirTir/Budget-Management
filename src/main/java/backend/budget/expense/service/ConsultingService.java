package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.repository.BudgetRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import backend.budget.expense.repository.ExpenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ConsultingService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public ConsultingService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public GuideExpenseResponse getTodayExpense(CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        LocalDate today = LocalDate.now();
        LocalDate startMonth = today.withDayOfMonth(1);
        LocalDate endMonth = today.withDayOfMonth(today.lengthOfMonth());

        // 전체 예산 조회 (월별 예산)
        List<Budget> budgets = budgetRepository.findByUserIdAndPeriod(user.getId(), startMonth);

        log.info("Retrieved budgets: " + budgets);

        long totalSpent = 0;
        Map<String, GuideExpenseResponse.CategoryExpenseDetail> categoryDetails = new HashMap<>();

        for (Budget budget : budgets) {
            String category = budget.getCategory().getName();

            // 오늘 사용한 금액 계산
            long spentToday = expenseRepository.getTotalExpenseByUserIdAndCategoryIdAndDateBetween(
                    user.getId(), budget.getCategory().getId(), today, today
            );

            log.info("Spent today for category " + category + ": " + spentToday);
            totalSpent += spentToday;

            // 적정 금액 계산
            long idealAmount = budget.getAmount() / today.lengthOfMonth();  // 오늘 적정 금액
            double riskPercentage = (double) spentToday / idealAmount * 100; // 위험도 계산

            // 지출 세부 정보 추가
            GuideExpenseResponse.CategoryExpenseDetail detail = new GuideExpenseResponse.CategoryExpenseDetail(
                    idealAmount, spentToday, riskPercentage
            );

            categoryDetails.put(category, detail);
        }
        log.info("Total spent today: " + totalSpent);
        return new GuideExpenseResponse(totalSpent, categoryDetails);
    }

    public SuggestExpenseResponse suggestExpense(CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        LocalDate today = LocalDate.now();
        LocalDate startMonth = today.withDayOfMonth(1);
        LocalDate endMonth = today.withDayOfMonth(today.lengthOfMonth());

        // 해당 월의 모든 카테고리 예산
        List<Budget> budgets = budgetRepository.findByUserIdAndPeriod(user.getId(), startMonth);

        if (budgets.isEmpty()) {
            throw new GeneralException(ErrorCode.BUDGET_NOT_FOUND);
        }

        long totalBudget = budgets.stream().mapToLong(Budget::getAmount).sum();
        long totalSpent = budgets.stream().mapToLong(budget ->
                expenseRepository.getTotalExpenseByUserIdAndCategoryIdAndDateBetween(
                        user.getId(), budget.getCategory().getId(), startMonth, today
                )
        ).sum();
        long remainBudget = totalBudget - totalSpent;
        long remainDays = ChronoUnit.DAYS.between(today, endMonth) + 1;

        log.info("Total Budget: {}, Total Spent: {}, Remaining Budget: {}, Remaining Days: {}",
                totalBudget, totalSpent, remainBudget, remainDays);

        Map<String, Long> categoryBudget = new HashMap<>(); // 카테고리별 남은 예산
        for(Budget budget : budgets){
            long spentCategory = expenseRepository.getTotalExpenseByUserIdAndCategoryIdAndDateBetween(
                    user.getId(), budget.getCategory().getId(), startMonth, today
            );
            long remainCategoryBudget = budget.getAmount() - spentCategory;
            long dailySuggestExpense = remainCategoryBudget / remainDays;

            // 음수로 떨어지지 않도록 최소 금액을 동적으로 보장
            long suggestExpense = Math.max(1000, Math.round(dailySuggestExpense / 100.0) * 100);
            categoryBudget.put(budget.getCategory().getName(), suggestExpense);
        }

        // 전체 예산 추천
        long dailySuggestTotalExpense = remainBudget / remainDays;
        dailySuggestTotalExpense = Math.max(1000, Math.round(dailySuggestTotalExpense / 100.0) * 100);

        // 소비 상태에 따른 메시지 생성
        String message = getMessage(totalSpent, totalBudget);

        return new SuggestExpenseResponse(dailySuggestTotalExpense, categoryBudget, message);
    }

    // 소비 상태에 따른 메시지
    private String getMessage(long amount, long totalBudget) {
        double percentage = (double) amount / totalBudget * 100;

        if (percentage > 50) {
            return "절약을 잘 실천하고 계세요! 오늘도 절약 도전!";
        } else if (percentage > 20) {
            return "적당히 사용 중이시네요! 오늘도 적당한 소비를 이어가세요.";
        } else if (percentage > 0) {
            return "예산의 대부분을 사용했습니다. 남은 기간을 생각해서 소비하세요!";
        } else {
            return "예산을 초과했습니다. 내일부터는 더욱 절약해보세요!";
        }
    }
}
