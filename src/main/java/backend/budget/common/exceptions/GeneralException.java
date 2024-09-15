package backend.budget.common.exceptions;

import backend.budget.common.constants.ErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{

    private final ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
