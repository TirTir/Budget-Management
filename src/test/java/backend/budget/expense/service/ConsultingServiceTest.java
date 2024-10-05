package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.BudgetRepository;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import backend.budget.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConsultingServiceTest {
    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ConsultingService consultingService;

    @Mock
    private User user;

    @Mock
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(customUserDetails.getUser()).thenReturn(user);
        System.out.println("Test Before");
    }

    @AfterEach
    public void after(){
        System.out.println("Test After");
    }

    @Test
    void testGetTodayExpense() {
        // Arrange
        Budget budget = Budget.builder()
                .amount(10000L)
                .category(new Category(1L, "식비"))
                .build();

        List<Budget> budgets = Collections.singletonList(budget);

        when(budgetRepository.findByUserIdAndPeriod(any(Long.class), any(LocalDate.class)))
                .thenReturn(budgets);
        when(expenseRepository.getTotalExpenseByUserIdAndCategoryIdAndDateBetween(
                any(Long.class), any(Long.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(500L);

        // Act
        GuideExpenseResponse response = consultingService.getTodayExpense(customUserDetails);

        // Assert
        verify(expenseRepository, times(1)).getTotalExpenseByUserIdAndCategoryIdAndDateBetween(
                any(Long.class), any(Long.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testSuggestExpense() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate endMonth = today.withDayOfMonth(today.lengthOfMonth());
        Category category = Category.builder().name("식비").build();

        Budget budget = Budget.builder()
                .amount(300000L)
                .category(category)
                .build();

        when(budgetRepository.findByUserIdAndPeriod(any(Long.class), any(LocalDate.class)))
                .thenReturn(List.of(budget));
        when(expenseRepository.getTotalExpenseByUserIdAndCategoryIdAndDateBetween(any(Long.class), any(Long.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(0L);  // 현재까지 지출된 금액

        // Act
        SuggestExpenseResponse response = consultingService.suggestExpense(customUserDetails);

        // Assert
        long expectedRemainingDays = ChronoUnit.DAYS.between(today, endMonth) + 1;
        long expectedDailySuggestedAmount = 300000L / expectedRemainingDays;
        long roundedSuggestedAmount = Math.max(1000, Math.round(expectedDailySuggestedAmount / 100.0) * 100);

        // 예상 금액과 실제 반환 값이 일치하는지 확인
        assertEquals(roundedSuggestedAmount, response.getTotalAmount());
    }
}