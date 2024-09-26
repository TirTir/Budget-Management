package backend.budget.budget.controller;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.budget.dto.BudgetResponse;
import backend.budget.budget.dto.SetBudgetRequest;
import backend.budget.budget.dto.SuggestBudgetRequest;
import backend.budget.budget.service.BudgetService;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.dto.ApiSuccessResponse;
import backend.budget.common.utils.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/budget")
@Tag(name = "Budget-Controller", description = "Budget API")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Operation(
            summary = "예산 설정 API"
    )
    @PostMapping("")
    public CommonResponse setBudget(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody SetBudgetRequest request) {
        log.info("[예산 설정 요청] ID: {}", customUserDetails.getUsername());
        budgetService.setBudget(customUserDetails, request);
        return CommonResponse.res(true, SuccessCode.SUCCESS_SET_BUDGET);
    }

    @Operation(
            summary = "예산 추천 API"
    )
    @PostMapping("/suggest")
    public ApiSuccessResponse<List<BudgetResponse>> suggestBudget(@Valid @RequestBody SuggestBudgetRequest request) {
        log.info("[예산 추천 요청]: ");
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_SUGGEST_BUDGET, budgetService.suggestBudget(request));
    }
}
