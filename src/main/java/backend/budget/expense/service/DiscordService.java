package backend.budget.expense.service;

import backend.budget.common.dto.DiscordMessage;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DiscordService {
    private final WebClient suggestClient;
    private final WebClient guideClient;

    public DiscordService(WebClient suggestClient, WebClient guideClient) {
        this.suggestClient = suggestClient;
        this.guideClient = guideClient;
    }

    public Mono<Void> sendSuggestExpense(SuggestExpenseResponse response) {
        DiscordMessage discordMessage = new DiscordMessage();
        discordMessage.setContent("오늘의 지출 추천");
        discordMessage.setData(response);

        return suggestClient.post()
                .bodyValue(discordMessage)
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .doOnError(error -> log.error("Runtime WebHook 전송 에러 - {}", error.getMessage()));
    }

    public Mono<Void> sendGuideExpense(GuideExpenseResponse response) {
        DiscordMessage discordMessage = new DiscordMessage();
        discordMessage.setContent("오늘의 지출 안내");
        discordMessage.setData(response);

        return suggestClient.post()
                .bodyValue(discordMessage)
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .doOnError(error -> log.error("Runtime WebHook 전송 에러 - {}", error.getMessage()));
    }
}
