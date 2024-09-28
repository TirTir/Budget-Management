package backend.budget.expense.controller;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.utils.CommonResponse;
import backend.budget.expense.dto.CreateExpenseRequest;
import backend.budget.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/expense")
@Tag(name = "Expense-Controller", description = "Expense API")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(
            summary = "지출 생성 API"
    )
    @PostMapping
    public CommonResponse createExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CreateExpenseRequest request){
        log.info("[지출 기록 요청] ID: {}", customUserDetails.getUsername());
        expenseService.createExpense(customUserDetails, request);
        return CommonResponse.res(true, SuccessCode.SUCCESS_CREATE_EXPENSE);
    }
}
