package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.CategoryRepository;
import backend.budget.expense.dto.CreateExpenseRequest;
import backend.budget.expense.entity.Expense;
import backend.budget.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ExpenseServiceTest {
    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private User user;

    @Mock
    private CustomUserDetails customUserDetails;

    private Category mockCategory;

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
    public void testCreateExpense(){
        // Arrange
        mockCategory = new Category();
        CreateExpenseRequest mockRequest = new CreateExpenseRequest(10000L, LocalDate.now(), "Test memo", 1L);
        when(categoryRepository.findById(mockRequest.getCategory()))
                .thenReturn(Optional.of(mockCategory));

        // Act
        expenseService.createExpense(customUserDetails, mockRequest);

        // Assert
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
}