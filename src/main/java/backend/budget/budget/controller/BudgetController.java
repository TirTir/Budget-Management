package backend.budget.budget.controller;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.entity.User;
import backend.budget.budget.dto.SetBudgetRequest;
import backend.budget.budget.service.BudgetService;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.utils.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            summary = "예산 API"
    )
    @PostMapping("")
    public CommonResponse setBudget(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody SetBudgetRequest request) {
        log.debug("Security context authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("[예산 설정 요청] ID: {}", customUserDetails.getUsername());
        budgetService.setBudget(customUserDetails, request);
        return CommonResponse.res(true, SuccessCode.SUCCESS_SET_BUDGET);
    }
}
