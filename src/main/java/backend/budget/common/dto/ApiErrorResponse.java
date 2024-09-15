package backend.budget.common.dto;

import backend.budget.common.constants.ErrorCode;
import backend.budget.common.utils.ApiResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiErrorResponse extends ApiResponse<String> {


    private final String message;

    private ApiErrorResponse(HttpStatus status, ErrorCode errorCode) {
        super(false, status);
        this.message = errorCode.getMessage();
    }

    public static ApiErrorResponse res(ErrorCode errorCode) {
        return new ApiErrorResponse(errorCode.getStatus(), errorCode);
    }
}