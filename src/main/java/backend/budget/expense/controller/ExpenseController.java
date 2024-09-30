package backend.budget.expense.controller;

import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.dto.ApiSuccessResponse;
import backend.budget.common.utils.CommonResponse;
import backend.budget.expense.dto.CreateExpenseRequest;
import backend.budget.expense.dto.DetailExpenseResponse;
import backend.budget.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        log.info("[지출 생성 요청] ID: {}", customUserDetails.getUsername());
        expenseService.createExpense(customUserDetails, request);
        return CommonResponse.res(true, SuccessCode.SUCCESS_CREATE_EXPENSE);
    }

//    @Operation(
//            summary = "지출 상세 API"
//    )
//    public ApiSuccessResponse<DetailExpenseResponse> detailExpense(){
//        log.info("[지출 상세 요청] ID:";
//        return ApiSuccessResponse.res(true, SuccessCode.SUCCESS_DETAIL_EXPENSE, expenseService.)
//    }

    @Operation(
            summary = "지출 수정 API"
    )
    @DeleteMapping("/{expenseId}")
    public CommonResponse updateExpense(@PathVariable Long expenseId){
        log.info("[지출 수정 요청] ID: {}", expenseId);
        expenseService.deleteExpense(expenseId);
        return CommonResponse.res(true, SuccessCode.SUCCESS_DETAIL_EXPENSE);
    }

    @Operation(
            summary = "지출 삭제 API"
    )
    @DeleteMapping("/{expenseId}")
    public CommonResponse detailExpense(@PathVariable Long expenseId){
        log.info("[지출 삭제 요청] ID: {}", expenseId);
        expenseService.deleteExpense(expenseId);
        return CommonResponse.res(true, SuccessCode.SUCCESS_DETAIL_EXPENSE);
    }

    @Operation(
            summary = "지출 합계 제외 API"
    )
    @PatchMapping("/{expenseId}/exclude")
    public CommonResponse excludeSum(@PathVariable Long expenseId) {
        log.info("[지출 합계 제외 요청] ID: {}", expenseId);
        expenseService.excludeSum(expenseId);
        return CommonResponse.res(true, SuccessCode.SUCCESS_EXCLUDE_EXPENSE);
    }
}
