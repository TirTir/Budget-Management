package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.CategoryRepository;
import backend.budget.expense.dto.CreateExpenseRequest;
import backend.budget.expense.dto.ExpenseRequest;
import backend.budget.expense.dto.ExpenseResponse;
import backend.budget.expense.dto.DetailExpenseResponse;
import backend.budget.expense.entity.Expense;
import backend.budget.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {
    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private Expense expense;

    @Mock
    private User user;

    @Mock
    private CustomUserDetails customUserDetails;

    private Category mockCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(customUserDetails.getUser()).thenReturn(user);
        mockCategory = new Category(); // 카테고리 객체 초기화
        System.out.println("Test Before");
    }

    @AfterEach
    public void after(){
        System.out.println("Test After");
    }

    @Test
    public void testCreateExpense(){
        // Arrange
        mockCategory = new Category();
        CreateExpenseRequest mockRequest = new CreateExpenseRequest(10000L, LocalDate.now(), "Test memo", 1L);
        when(categoryRepository.findById(mockRequest.getCategoryId()))
                .thenReturn(Optional.of(mockCategory));

        // Act
        expenseService.createExpense(customUserDetails, mockRequest);

        // Assert
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    public void testUpdateExpense() {
        // Arrange
        Long expenseId = 1L;
        Long categoryId = 2L;
        CreateExpenseRequest request = new CreateExpenseRequest(10000L, LocalDate.now(), "Updated memo", categoryId);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense)); // Mock Expense 반환
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));

        // Act
        expenseService.updateExpense(expenseId, request);

        // Assert
        // Assert
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(expense, times(1)).updateExpense(request.getAmount(), request.getExpenseDate(), mockCategory, request.getMemo());
    }

    @Test
    public void testDeleteExpense() {
        // Arrange
        Long expenseId = 1L;

        // Act
        expenseService.deleteExpense(expenseId);

        // Assert
        verify(expenseRepository, times(1)).deleteById(expenseId);
    }

    @Test
    public void testExcludeSum() {
        // Arrange
        Long expenseId = 1L;
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act
        expenseService.excludeSum(expenseId);

        // Assert
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expense, times(1)).setExcludedSum();
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    void testGetExpense() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        Category category = new Category(1L, "식비");
        Expense expense1 = new Expense(1L, 10000L, LocalDate.of(2023, 1, 15), "Memo 1", false, category, null);
        Expense expense2 = new Expense(2L, 20000L, LocalDate.of(2023, 1, 20), "Memo 2", false, category, null);

        List<Expense> expenses = Arrays.asList(expense1, expense2);

        when(expenseRepository.findByExpenseDateBetween(startDate, endDate)).thenReturn(expenses);

        // Act
        ExpenseResponse response = expenseService.getExpense(startDate, endDate, new ExpenseRequest());

        // Assert
        assertEquals(2, response.getExpenses().size());
        assertEquals(30000L, response.getTotalExpense());
        assertEquals(1, response.getCategoryExpense().size());
        assertEquals(30000L, response.getCategoryExpense().get("식비"));

        verify(expenseRepository, times(1)).findByExpenseDateBetween(startDate, endDate);
    }

    @Test
    void testDetailExpense() {
        // Arrange
        Long expenseId = 1L;
        Category category = new Category(1L, "식비");
        Expense expense = new Expense(1L, 10000L, LocalDate.now(), "Memo", false, category, null);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        // Act
        DetailExpenseResponse response = expenseService.detailExpense(expenseId);

        // Assert
        assertEquals(10000L, response.getAmount());
        assertEquals("Memo", response.getMemo());
        assertEquals("식비", response.getCategoryName());

        verify(expenseRepository, times(1)).findById(expenseId);
    }
}