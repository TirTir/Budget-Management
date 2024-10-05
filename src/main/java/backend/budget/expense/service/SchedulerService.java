package backend.budget.expense.service;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SchedulerService {

    private final UserRepository userRepository;
    private final ConsultingService consultingService;
    private final DiscordService discordService;

    public SchedulerService(UserRepository userRepository, ConsultingService consultingService, DiscordService discordService) {
        this.userRepository = userRepository;
        this.consultingService = consultingService;
        this.discordService = discordService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void suggestRun() {
        log.info("스케줄러가 실행되었습니다.");

        List<User> users = userRepository.findByDiscordWebhookUrlIsNotNull();
        // 각 유저에게 알림 전송
        for (User user : users) {
            String discordWebhookUrl = user.getDiscordWebhookUrl();
            log.info("유저 {}에게 알림을 전송합니다. URL: {}", user.getUserName(), discordWebhookUrl);

            SuggestExpenseResponse response = consultingService.suggestExpense(new CustomUserDetails(user));

            // 디스코드로 알림 전송
            discordService.sendSuggestExpense(discordWebhookUrl, response).subscribe();
        }
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void guideRun() {
        log.info("스케줄러가 실행되었습니다.");
        List<User> users = userRepository.findByDiscordWebhookUrlIsNotNull();
        // 각 유저에게 알림 전송
        for (User user : users) {
            String discordWebhookUrl = user.getDiscordWebhookUrl();
            log.info("유저 {}에게 알림을 전송합니다. URL: {}", user.getUserName(), discordWebhookUrl);

            GuideExpenseResponse response = consultingService.getTodayExpense(new CustomUserDetails(user));

            // 디스코드로 알림 전송
            discordService.sendGuideExpense(discordWebhookUrl, response).subscribe();
        }
    }
}
