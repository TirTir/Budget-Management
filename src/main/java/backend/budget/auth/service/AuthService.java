package backend.budget.auth.service;

import backend.budget.auth.dto.AuthRequest;
import backend.budget.auth.dto.AuthResponse;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import backend.budget.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthResponse auth(AuthRequest request) {
        // 계정 여부 확인
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(passwordMatches){
            log.info("Create token : {}", user.getUserName());

        } else {
            throw new BadCredentialsException(ErrorCode.PASSWORD_MISMATCH.getMessage());
        }
        return null;
    }
}
