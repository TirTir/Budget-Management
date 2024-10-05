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
    SUCCESS_UPDATE(HttpStatus.OK, "정보 업데이트 성공"),

    SUCCESS_CATEGORY_LIST(HttpStatus.OK, "카테고리 목록 반환 성공"),
    SUCCESS_SET_BUDGET(HttpStatus.OK, "예산 설정 성공"),
    SUCCESS_SUGGEST_BUDGET(HttpStatus.OK, "예산 추천 성공"),

    SUCCESS_CREATE_EXPENSE(HttpStatus.OK, "지출 생성 성공"),
    SUCCESS_EXPENSE_LIST(HttpStatus.OK, "지출 목록 반환 성공"),
    SUCCESS_DETAIL_EXPENSE(HttpStatus.OK, "지출 상세목록 반환 성공"),
    SUCCESS_UPDATE_EXPENSE(HttpStatus.OK, "지출 업데이트 성공"),
    SUCCESS_DELETE_EXPENSE(HttpStatus.OK, "지출 삭제 성공"),
    SUCCESS_EXCLUDE_EXPENSE(HttpStatus.OK, "지출 합계 제외 성공"),

    SUCCESS_COMPARE_MONTH(HttpStatus.OK, "지난달 대비 소비율 비교 조회 성공"),
    SUCCESS_COMPARE_WEEK(HttpStatus.OK, "지난 요일 대비 소비율 비교 조회 성공"),
    SUCCESS_COMPARE_USER(HttpStatus.OK, "다른 사용자 대비 소비율 조회 성공");

    private final HttpStatus status;
    private final String message;
}
