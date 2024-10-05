package backend.budget.expense.controller;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.dto.ApiSuccessResponse;
import backend.budget.expense.dto.GuideExpenseResponse;
import backend.budget.expense.dto.SuggestExpenseResponse;
import backend.budget.expense.service.ConsultingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/expense/consulting")
@Tag(name = "Cunsulting-Controller", description = "Cunsulting API")
public class ConsultingController {

    private final ConsultingService consultingService;

    public ConsultingController(ConsultingService consultingService) {
        this.consultingService = consultingService;
    }


    @Operation(
            summary = "오늘의 지출 정보 조회 API"
    )
    @GetMapping("/today")
    public ApiSuccessResponse<GuideExpenseResponse> getTodayExpense(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[오늘의 지출 정보 조회 요청] ID: {}", userDetails.getUsername());
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_TODAY_EXPENSE, consultingService.getTodayExpense(userDetails));
    }

    @Operation(
            summary = "지출 추천 정보 조회 API"
    )
    @GetMapping("/suggest")
    public ApiSuccessResponse<SuggestExpenseResponse> SuggestExpenseResponse(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[지출 추천 정보 조회 요청] ID: {}", userDetails.getUsername());
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_SUGGEST_EXPENSE, consultingService.suggestExpense(userDetails));
    }
}
