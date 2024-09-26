package backend.budget.budget.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.dto.SetBudgetRequest;
import backend.budget.budget.dto.SuggestBudgetRequest;
import backend.budget.budget.dto.BudgetResponse;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.BudgetRepository;
import backend.budget.budget.repository.CategoryRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void setBudget(CustomUserDetails customUserDetails, SetBudgetRequest request){
        User user = customUserDetails.getUser();
        Category category = categoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));

        Budget budget = budgetRepository.findByUserAndCategory(user, category);

        if(budget != null){
            log.info("Budget Information - ID: {}", budget.getId());
            log.info("Request Data - Amount: {}, Period: {}", request.getAmount(), request.getPeriod());

            budget.updateBudget(request.getAmount(), request.getPeriod());
            log.info("Budget Information - Amount: {}", budget.getAmount());

            budgetRepository.save(budget);
        } else {
            Budget newBudget = Budget.builder()
                    .category(category)
                    .amount(request.getAmount())
                    .period(request.getPeriod())
                    .user(user)
                    .build();

            budgetRepository.save(newBudget);
        }
    }

    @Transactional
    public List<BudgetResponse> suggestBudget(SuggestBudgetRequest request){
        Long totalBudget = request.getTotalBudget();
        List<Object[]> avgBudgets = budgetRepository.findAverageBudgetByCategory();

        Map<String, Double> categoryPercentages = new HashMap<>(); // 카테고리 별 평균 예산
        double totalPercentage = avgBudgets.stream()
                .mapToDouble(b -> (Double) b[1]) // 각 카테고리 평균 예산
                .sum(); // 평균 예산 합계

        for(Object[] avgBudget : avgBudgets) {
            String category = (String) avgBudget[0];
            Double avgAmount = (Double) avgBudget[1]; // 카테고리 평균

            double percentage = avgAmount / totalPercentage;
            categoryPercentages.put(category, percentage * 100);  // 비율을 %로 변환
        }

        // 10% 이하의 항목들
        double etcPercentage = 0.0;
        List<String> etcCategories = new ArrayList<>();

        for(String category : categoryPercentages.keySet()) {
            double percentage = categoryPercentages.get(category);

            if(percentage <= 10.0) {
                etcPercentage += percentage;
                etcCategories.add(category);
            }
        }

        List<BudgetResponse> suggestBudgets = new ArrayList<>();

        for(String category : categoryPercentages.keySet()) {
            if(!etcCategories.contains(category)){
                BudgetResponse response = new BudgetResponse();
                response.setCategory(category);

                // 비율을 계산하여 추천 금액 설정
                double percentage = categoryPercentages.get(category);
                long amount = Math.round(totalBudget * percentage / 100);
                response.setAmount(amount);

                // 추천 예산 리스트에 추가
                suggestBudgets.add(response);
            }
        }

        if (etcPercentage > 0) {
            BudgetResponse response = new BudgetResponse();
            response.setCategory("기타");

            long amount = Math.round(totalBudget * etcPercentage);
            response.setAmount(amount);

            suggestBudgets.add(response);
        }

        return suggestBudgets;
    }
}
