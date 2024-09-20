package backend.budget.common.dto;

import backend.budget.common.constants.SuccessCode;
import lombok.Getter;
import backend.budget.common.utils.CommonResponse;

@Getter
public class ApiSuccessResponse<T> extends CommonResponse {

    private final T data;

    private ApiSuccessResponse(String message, T data) {
        super(true, message);
        this.data = data;
    }

    public static<T> ApiSuccessResponse<T> res(SuccessCode status, T data) {
        return new ApiSuccessResponse<>(status.getMessage(), data);
    }
}
