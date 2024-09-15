package backend.budget.common.exceptions;

import backend.budget.common.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(GeneralException e) {
        log.error("GeneralException 발생", e);
        ApiErrorResponse errorResponse = ApiErrorResponse.res(e.getErrorCode());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
