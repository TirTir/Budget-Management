package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.CategoryRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import backend.budget.expense.dto.CreateExpenseRequest;
import backend.budget.expense.entity.Expense;
import backend.budget.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public ExpenseService(CategoryRepository categoryRepository, ExpenseRepository expenseRepository) {
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public void createExpense(CustomUserDetails customUserDetails, CreateExpenseRequest request){
        User user = customUserDetails.getUser();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .memo(request.getMemo())
                .category(category)
                .user(user)
                .build();

        expenseRepository.save(expense);
    }

    @Transactional
    public void updateExpense(Long expenseId, CreateExpenseRequest request){
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new GeneralException(ErrorCode.EXPENSE_NOT_FOUND));

        Category category = null;
        if(request.getCategoryId() != null){
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        expense.updateExpense(request.getAmount(), request.getExpenseDate(), category, request.getMemo());
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    @Transactional
    public void excludeSum(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new GeneralException(ErrorCode.EXPENSE_NOT_FOUND));
        expense.setExcludedSum();
        expenseRepository.save(expense);
    }
}
