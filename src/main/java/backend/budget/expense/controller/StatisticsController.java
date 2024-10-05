package backend.budget.expense.controller;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.dto.ApiSuccessResponse;
import backend.budget.expense.service.ExpenseStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/expense/statistics")
@Tag(name = "Statistics-Controller", description = "Statistics API")
public class StatisticsController {

    private final ExpenseStatisticsService expenseStatisticsService;

    public StatisticsController(ExpenseStatisticsService expenseStatisticsService) {
        this.expenseStatisticsService = expenseStatisticsService;
    }

    @Operation(
            summary = "지난달 대비 소비율 비교 API"
    )
    @GetMapping("/month")
    public ApiSuccessResponse<Double> compareLastMonth(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam String date){
        log.info("[지난달 대비 소비율 비교 요청] ID: {} ", customUserDetails.getUsername());
        LocalDate localDate = LocalDate.parse(date);
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_COMPARE_MONTH, expenseStatisticsService.compareLastMonth(customUserDetails, localDate));
    }

    @Operation(
            summary = "지난 요일 대비 소비율 비교 API"
    )
    @GetMapping("/week")
    public ApiSuccessResponse<Double> compareWeek(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam String date){
        log.info("[지난 요일 대비 소비율 비교 요청] ID: {} ", customUserDetails.getUsername());
        LocalDate localDate = LocalDate.parse(date);
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_COMPARE_WEEK, expenseStatisticsService.compareWeek(customUserDetails, localDate));
    }

    @Operation(
            summary = "다른 사용자 대비 소비율 비교 API"
    )
    @GetMapping("/otherUser")
    public ApiSuccessResponse<Double> compareOtherUsers(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam String date){
        log.info("[다른 사용자 대비 소비율 비교 요청] ID: {} ", customUserDetails.getUsername());
        LocalDate localDate = LocalDate.parse(date);
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_COMPARE_WEEK, expenseStatisticsService.compareWithOtherUsers(customUserDetails, localDate));
    }
}
