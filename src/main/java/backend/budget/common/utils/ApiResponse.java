package backend.budget.common.utils;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
    private final Boolean success;
    private final HttpStatus status;

    protected ApiResponse(Boolean success, HttpStatus status) {
        this.success = success;
        this.status = status;
    }

    public static ApiResponse res(Boolean success, HttpStatus status) {
        return new ApiResponse(success, status);
    }
}
