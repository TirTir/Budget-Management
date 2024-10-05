package backend.budget.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 2, max = 8, message = "아이디는 2~8자 사이로 입력해주세요.")
    private String userName;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&.])[A-Za-z\\d$@$!%*#?&.]{8,20}$",
            message = "비밀번호는 영문자와 특수문자를 포함하여 8자 이상 20자 이하로 입력해주세요."
    )
    private String password;

    private String discordWebhookUrl;
}
