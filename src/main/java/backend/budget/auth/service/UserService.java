package backend.budget.auth.service;

import backend.budget.auth.dto.AuthRequest;
import backend.budget.auth.dto.AuthResponse;
import backend.budget.auth.dto.RegisterRequest;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public void register(RegisterRequest request) {

        // 계정 중복 확인
        if(userRepository.existsByUserName(request.getUserName())){
            throw new GeneralException(ErrorCode.USERNAME_ALREADY_EXIST);
        }

        User new_user =User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(new_user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        // 계정 여부 확인
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(passwordMatches){
            log.info("Create token for user: {}", user.getUserName());
            return authService.getAuthToken(user);
        } else {
            throw new BadCredentialsException(ErrorCode.PASSWORD_MISMATCH.getMessage());
        }
    }
}
