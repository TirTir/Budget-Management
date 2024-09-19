package backend.budget.auth.service;

import backend.budget.auth.dto.AuthResponse;
import backend.budget.auth.entity.RefreshToken;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.RefreshTokenRepository;
import backend.budget.auth.repository.UserRepository;
import backend.budget.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional(readOnly = true)
    public AuthResponse getAuthToken(User user){
        log.info("Attempting authentication for user: {}", user.getUserName());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // RefreshToken 저장
        RefreshToken redis = new RefreshToken(refreshToken, accessToken);
        refreshTokenRepository.save(redis);
        log.info("RefreshToken saved ID: {}", user.getUserName());

        return new AuthResponse(accessToken, refreshToken);
    }
}
