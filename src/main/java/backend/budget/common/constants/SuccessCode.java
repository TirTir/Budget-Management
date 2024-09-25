package backend.budget.common.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    SUCCESS_SIGNUP(HttpStatus.OK, "회원가입 성공"),
    SUCCESS_SIGNIN(HttpStatus.OK, "로그인 성공"),
    SUCCESS_TOKEN_REFRESH(HttpStatus.OK, "AccessToken 갱신 성공"),
    SUCCESS_LOGOUT(HttpStatus.OK, "로그아웃 성공"),

    SUCCESS_CATEGORY_LIST(HttpStatus.OK, "카테고리 목록 반환 성공"),
    SUCCESS_SET_BUDGET(HttpStatus.OK, "예산 설정 성공");

    private final HttpStatus status;
    private final String message;
}
