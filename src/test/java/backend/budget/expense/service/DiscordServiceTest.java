package backend.budget.expense.service;

import backend.budget.common.dto.DiscordMessage;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DiscordServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private DiscordService discordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(DiscordMessage.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));
    }

    @Test
    void testSendSuggestExpense() {
        // Arrange
        SuggestExpenseResponse response = new SuggestExpenseResponse(10000L, null, "Test Suggestion");
        String webhookUrl = "https://discord.com/api/webhooks/1291992630787571794/PFyoF3DzXbkfPfOALrT04VtJtyp4Flz3oUUjnITc04in6M-f3CI1UQ_lo4DAdIN5BYBn";

        // Act
        discordService.sendSuggestExpense(webhookUrl, response).block();

        // Assert
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri(webhookUrl);
        verify(requestBodySpec, times(1)).bodyValue(any(DiscordMessage.class));
        verify(requestHeadersSpec, times(1)).retrieve();
    }

    @Test
    void testSendGuideExpense() {
        // Arrange
        GuideExpenseResponse response = new GuideExpenseResponse(8000L, null);
        String webhookUrl = "https://discord.com/api/webhooks/1291992630787571794/PFyoF3DzXbkfPfOALrT04VtJtyp4Flz3oUUjnITc04in6M-f3CI1UQ_lo4DAdIN5BYBn";

        // Act
        discordService.sendGuideExpense(webhookUrl, response).block();

        // Assert
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri(webhookUrl);
        verify(requestBodySpec, times(1)).bodyValue(any(DiscordMessage.class));
        verify(requestHeadersSpec, times(1)).retrieve();
    }
}
