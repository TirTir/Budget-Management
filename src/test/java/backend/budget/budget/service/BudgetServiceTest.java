package backend.budget.budget.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.dto.BudgetResponse;
import backend.budget.budget.dto.SetBudgetRequest;
import backend.budget.budget.dto.SuggestBudgetRequest;
import backend.budget.budget.entity.Budget;
import backend.budget.budget.entity.Category;
import backend.budget.budget.repository.BudgetRepository;
import backend.budget.budget.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BudgetServiceTest {
    @InjectMocks
    private BudgetService budgetService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private User user;

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
    public void testSetBudget_updatesBudget(){
        // Arrange
        Category category = new Category(1L, "식비");
        SetBudgetRequest request = new SetBudgetRequest(1L, 50000L, LocalDate.now());
        Budget existingBudget = new Budget(1L, 40000L, LocalDate.now(), user, category);

        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));
        when(budgetRepository.findByUserAndCategory(user, category)).thenReturn(null);

        // Act
        budgetService.setBudget(customUserDetails, request);

        // Assert
        ArgumentCaptor<Budget> budgetCaptor = ArgumentCaptor.forClass(Budget.class);
        verify(budgetRepository).save(budgetCaptor.capture());

        Budget savedBudget = budgetCaptor.getValue();
        assertEquals(50000L, savedBudget.getAmount());
    }

    @Test
    public void testSetBudget_createsBudget() {
        // Arrange
        Category category = new Category(1L, "식비");
        SetBudgetRequest request = new SetBudgetRequest(1L, 50000L, LocalDate.now());

        when(categoryRepository.findById(request.getCategory())).thenReturn(Optional.of(category));
        when(budgetRepository.findByUserAndCategory(user, category)).thenReturn(null);

        // Act
        budgetService.setBudget(customUserDetails, request);

        // Assert
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    public void testSuggestBudget() {
        // Arrange
        SuggestBudgetRequest request = new SuggestBudgetRequest(1000000L); // 총 예산 설정

        // 가짜 데이터 설정
        List<Object[]> avgBudgets = new ArrayList<>();
        avgBudgets.add(new Object[]{"식비", 40.0});
        avgBudgets.add(new Object[]{"주거", 30.0});
        avgBudgets.add(new Object[]{"교통", 23.0});
        avgBudgets.add(new Object[]{"취미", 7.0});

        when(budgetRepository.findAverageBudgetByCategory()).thenReturn(avgBudgets);

        // Act
        List<BudgetResponse> responses = budgetService.suggestBudget(request);

        // Assert
        assertEquals(4, responses.size());

        for (BudgetResponse response : responses) {
            switch (response.getCategory()) {
                case "식비":
                    assertEquals(400000L, response.getAmount());
                    break;
                case "주거":
                    assertEquals(300000L, response.getAmount());
                    break;
                case "교통":
                    assertEquals(230000L, response.getAmount());
                    break;
                case "기타":
                    assertEquals(70000L, response.getAmount());
                    break;
            }
        }
    }
}
