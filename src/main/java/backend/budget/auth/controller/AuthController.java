package backend.budget.auth.controller;

import backend.budget.auth.dto.RegisterRequest;
import backend.budget.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Auth-Controller", description = "Auth API")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
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
    public void signup(@RequestBody @Valid RegisterRequest request){
        log.info("[회원가입 요청] ID: {}", request.getUserName());
        userService.register(request);
    }
}
