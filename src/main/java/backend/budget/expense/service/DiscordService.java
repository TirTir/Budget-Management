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
    private final WebClient webClient;

    public DiscordService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> sendSuggestExpense(String webHookUrl, SuggestExpenseResponse response) {
        DiscordMessage discordMessage = new DiscordMessage();
        discordMessage.setContent("오늘의 지출 추천");
        discordMessage.setData(response);

        log.info("Discord Webhook URL: {}", webHookUrl);
        log.info("Discord Message: {}", discordMessage);

        return webClient.post()
                .uri(webHookUrl)
                .bodyValue(discordMessage)
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("Discord 응답 실패: {}", errorBody))
                                .then(Mono.error(new RuntimeException("Discord Webhook 전송 실패")))
                )
                .bodyToMono(String.class)
                .doOnNext(responseBody -> log.info("Discord 응답: {}", responseBody)) // 응답 로그 추가
                .then()
                .doOnError(error -> log.error("디스코드 알림 전송 실패: {}", error.getMessage()));
    }

    public Mono<Void> sendGuideExpense(String webHookUrl, GuideExpenseResponse response) {
        DiscordMessage discordMessage = new DiscordMessage();
        discordMessage.setContent("오늘의 지출 안내");
        discordMessage.setData(response);

        log.info("Discord Webhook URL: {}", webHookUrl);
        log.info("Discord Message: {}", discordMessage);

        return webClient.post()
                .uri(webHookUrl)
                .bodyValue(discordMessage)
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .doOnError(error -> System.err.println("디스코드 알림 전송 실패: " + error.getMessage()));
    }
}
