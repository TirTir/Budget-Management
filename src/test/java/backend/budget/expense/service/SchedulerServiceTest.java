package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import backend.budget.budget.repository.BudgetRepository;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import backend.budget.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SchedulerServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsultingService consultingService;

    @Mock
    private DiscordService discordService;

    @InjectMocks
    private SchedulerService schedulerService;

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
    void testSuggestRun() {
        // Arrange
        User user = User.builder()
                .userName("TestUser")
                .discordWebhookUrl("https://discord.com/api/webhooks/1291992630787571794/PFyoF3DzXbkfPfOALrT04VtJtyp4Flz3oUUjnITc04in6M-f3CI1UQ_lo4DAdIN5BYBn")
                .build();

        List<User> users = List.of(user);

        Map<String, Long> categoryBudget = new HashMap<>();
        categoryBudget.put("식비", 5000L);
        long totalAmount = 10000L;
        String message = "오늘의 추천 지출입니다.";

        when(consultingService.suggestExpense(any())).thenReturn(new SuggestExpenseResponse(totalAmount, categoryBudget, message));
        when(discordService.sendSuggestExpense(anyString(), any(SuggestExpenseResponse.class)))
                .thenReturn(Mono.empty());
        when(userRepository.findByDiscordWebhookUrlIsNotNull()).thenReturn(users);

        // Act
        schedulerService.suggestRun();

        // Assert
        verify(userRepository, times(1)).findByDiscordWebhookUrlIsNotNull();
        verify(consultingService, times(1)).suggestExpense(any());
        verify(discordService, times(1)).sendSuggestExpense(anyString(), any(SuggestExpenseResponse.class));
    }

    @Test
    void testGuideRun() {
        // Arrange
        User user = User.builder()
                .userName("TestUser")
                .discordWebhookUrl("https://discord.com/api/webhooks/1291992630787571794/PFyoF3DzXbkfPfOALrT04VtJtyp4Flz3oUUjnITc04in6M-f3CI1UQ_lo4DAdIN5BYBn")
                .build();

        List<User> users = List.of(user);

        long totalSpent = 8000L;

        Map<String, GuideExpenseResponse.CategoryExpenseDetail> categoryExpenseDetails = new HashMap<>();
        categoryExpenseDetails.put("식비", new GuideExpenseResponse.CategoryExpenseDetail(10000L, 8000L, 80.0));

        when(consultingService.getTodayExpense(any())).thenReturn(new GuideExpenseResponse(totalSpent, categoryExpenseDetails));
        when(discordService.sendGuideExpense(anyString(), any(GuideExpenseResponse.class)))
                .thenReturn(Mono.empty());
        when(userRepository.findByDiscordWebhookUrlIsNotNull()).thenReturn(users);

        // Act
        schedulerService.guideRun();

        // Assert
        verify(userRepository, times(1)).findByDiscordWebhookUrlIsNotNull();
        verify(consultingService, times(1)).getTodayExpense(any());
        verify(discordService, times(1)).sendGuideExpense(anyString(), any(GuideExpenseResponse.class));
    }
}