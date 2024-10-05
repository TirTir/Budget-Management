package backend.budget.auth.controller;

import backend.budget.auth.dto.*;
import backend.budget.auth.entity.CustomUserDetails;
import backend.budget.auth.service.AuthService;
import backend.budget.auth.service.UserService;
import backend.budget.common.constants.SuccessCode;
import backend.budget.common.dto.ApiSuccessResponse;
import backend.budget.common.utils.CommonResponse;
import backend.budget.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Auth-Controller", description = "Auth API")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @Operation(
            summary = "회원가입 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SUCCESS_SIGNUP"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "USERNAME_ALREADY_EXIST"
                    )
            }
    )
    @PostMapping("/signup")
    public CommonResponse signup(@RequestBody @Valid RegisterRequest request){
        log.info("[회원가입 요청] ID: {}", request.getUserName());
        userService.register(request);
        return CommonResponse.res(true, SuccessCode.SUCCESS_SIGNUP);
    }

    @Operation(
            summary = "로그인 API"
    )
    @PostMapping("/signin")
    public ApiSuccessResponse<AuthResponse> signin(@RequestBody @Valid AuthRequest request){
        log.info("[로그인 요청] ID: {}", request.getUserName());
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_SIGNIN, userService.login(request));
    }

    @Operation(
            summary = "AccessToken 갱신 API"
    )
    @PostMapping("/refresh")
    public ApiSuccessResponse<RefreshResponse> refresh(HttpServletRequest request,
                                                       @RequestBody RefreshRequest refreshRequest){
        log.info("[AccessToken 갱신 요청]: ");
        String accessToken = jwtTokenProvider.resolveToken(request);
        return ApiSuccessResponse.res(SuccessCode.SUCCESS_TOKEN_REFRESH, authService.getRefresh(accessToken, refreshRequest.getRefreshToken()));
    }

    @Operation(
            summary = "로그아웃 API"
    )
    @PostMapping("/logout")
    public CommonResponse logout(HttpServletRequest request,
                                 @RequestBody RefreshRequest refreshRequest){
        log.info("[로그아웃 요청]: ");
        String accessToken = jwtTokenProvider.resolveToken(request);
        userService.logout(accessToken, refreshRequest.getRefreshToken());
        return CommonResponse.res(true, SuccessCode.SUCCESS_LOGOUT);
    }

    @PutMapping("/{userId}/discord")
    public CommonResponse<Void> updateDiscordWebhookUrl(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody String newWebhookUrl) {
        log.info("[디스코드 웹훅 수정 요청] ID: {}", customUserDetails.getUsername());
        userService.updateDiscordWebhookUrl(customUserDetails, newWebhookUrl);
        return CommonResponse.res(true, SuccessCode.SUCCESS_UPDATE);
    }
}
