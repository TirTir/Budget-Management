package backend.budget.common.dto;

import backend.budget.common.utils.ApiResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiErrorResponse extends ApiResponse<String> {


    private final String message;

    public ApiErrorResponse(String message, HttpStatus status) {
        super(false, status);
        this.message = message;
    }

    public static ApiErrorResponse res(String message, HttpStatus status) {
        return new ApiErrorResponse(message, status);
    }
}