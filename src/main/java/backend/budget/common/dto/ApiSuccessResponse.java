package backend.budget.common.dto;

import lombok.Getter;
import backend.budget.common.utils.ApiResponse;
import org.springframework.http.HttpStatus;

@Getter
public class ApiSuccessResponse<T> extends ApiResponse {

    private final T data;

    private ApiSuccessResponse(HttpStatus status, T data) {
        super(true, status);
        this.data = data;
    }

    public static<T> ApiSuccessResponse<T> res(HttpStatus status, T data) {
        return new ApiSuccessResponse<>(status, data);
    }
}
