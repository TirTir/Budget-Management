package backend.budget.common.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    USERNAME_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 계정입니다.");

    private final HttpStatus status;
    private final String message;
}
