package backend.budget.common.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // JWT
    JWT_VERIFICATION(HttpStatus.UNAUTHORIZED, "토큰 검증에 실패하였습니다."),
    INVALID_SECRET_KEY(HttpStatus.BAD_REQUEST, "JWT 시크릿 키 설정이 잘못되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh 토큰입니다."),
    MALFORMED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 형식의 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    SIGNATURE_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 서명 검증에 실패하였습니다."),

    // 계정
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "이미 로그아웃된 계정입니다"),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    USERNAME_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 계정입니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 사용자 계정을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 사용자를 찾을 수 없습니다."),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 카테고리를 찾을 수 없습니다."),
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 지출을 찾을 수 없습니다."),
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "설정된 예산을 찾을 수 없습니다."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    FORBIDDEN_RESOURCE(HttpStatus.FORBIDDEN, "이 리소스에 접근할 권한이 없습니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 입력 필드가 누락되었습니다."),


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다. 관리자에게 문의하세요.");

    private final HttpStatus status;
    private final String message;
}
