package backend.budget.budget.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.dto.SetBudgetRequest;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.BudgetRepository;
import backend.budget.budget.repository.CategoryRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
