package backend.budget.common.utils;

import backend.budget.common.constants.ErrorCode;
import backend.budget.common.constants.SuccessCode;
import lombok.*;
import org.springframework.http.HttpStatus;

@Data @Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;

    private ApiResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static<T> ApiResponse<T> res(SuccessCode success) {
        return new ApiResponse<>(success.getStatus(), success.getMessage());
    }

    public static<T> ApiResponse<T> res(ErrorCode error) {
        return new ApiResponse<>(error.getStatus(), error.getMessage());
    }

    public static<T> ApiResponse<T> res(SuccessCode successCode, final T data) {
        return ApiResponse.<T>builder()
                .status(successCode.getStatus())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }
}
