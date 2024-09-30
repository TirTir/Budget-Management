package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.CategoryRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import backend.budget.expense.dto.CreateExpenseRequest;
import backend.budget.expense.dto.DetailExpenseResponse;
import backend.budget.expense.dto.ExpenseRequest;
import backend.budget.expense.dto.ExpenseResponse;
import backend.budget.expense.entity.Expense;
import backend.budget.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Category category = expense.getCategory();
        if(request.getCategoryId() != null){
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Long amount = (request != null) ? request.getAmount() : expense.getAmount();
        LocalDate expenseDate = (request != null) ? request.getExpenseDate() : expense.getExpenseDate();
        String memo = (request != null) ? request.getMemo() : expense.getMemo();

        expense.updateExpense(amount, expenseDate, category, memo);
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

    @Transactional(readOnly = true)
    public ExpenseResponse getExpense(LocalDate startDate, LocalDate endDate, ExpenseRequest request){
        // 기간 조회
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);

        List<Expense> filteredExpenses = expenses.stream()
                .filter(expense -> request.getCategoryId() == null || expense.getCategory().getId().equals(request.getCategoryId()))  // 카테고리 필터
                .filter(expense -> (request.getMinAmount() == null || expense.getAmount() >= request.getMinAmount()) &&
                        (request.getMaxAmount() == null || expense.getAmount() <= request.getMaxAmount()))  // 금액 필터
                .filter(expense -> !expense.isExcludedSum())  // excludeSum 필터
                .collect(Collectors.toList());

        List<DetailExpenseResponse> detailResponses = expenses.stream()
                .map(expense -> DetailExpenseResponse.builder()
                        .amount(expense.getAmount())
                        .expenseDate(expense.getExpenseDate())
                        .memo(expense.getMemo())
                        .categoryName(expense.getCategory().getName())
                        .build())
                .collect(Collectors.toList());

        // 지출 합계
        Long totalExpense = detailResponses.stream()
                .mapToLong(DetailExpenseResponse::getAmount)
                .sum();

        // 카테고리별 지출 합계
        Map<String, Long> categoryExpense = expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getCategory().getName(),
                        Collectors.summingLong(Expense::getAmount)));

        ExpenseResponse response = ExpenseResponse.builder()
                .expenses(detailResponses)
                .totalExpense(totalExpense)
                .categoryExpense(categoryExpense)
                .build();

        return response;
    }

    @Transactional(readOnly = true)
    public DetailExpenseResponse detailExpense(Long id){
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorCode.EXPENSE_NOT_FOUND));

        return DetailExpenseResponse.builder()
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .categoryName(expense.getCategory().getName())
                .memo(expense.getMemo())  // 메모 추가
                .build();
    }
}
