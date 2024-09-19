package backend.budget.auth.service;

import backend.budget.auth.dto.AuthResponse;
import backend.budget.auth.entity.RefreshToken;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.RefreshTokenRepository;
import backend.budget.auth.repository.UserRepository;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
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
    private final UserRepository userRepository;
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
        RefreshToken redis = new RefreshToken(accessToken, refreshToken, user.getUserName());
        refreshTokenRepository.save(redis);
        log.info("RefreshToken saved ID: {}", user.getUserName());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public String getRefresh(String refreshToken){
        // redis 엔티티 조회
        RefreshToken redis = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new GeneralException(ErrorCode.INVALID_AUTH_TOKEN));

        User user = userRepository.findByUsername(redis.getUserName())
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // AccessToken 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        // redis AccessToken 업데이트
        redis.updateAccessToken(newAccessToken);
        refreshTokenRepository.save(redis);

        return newAccessToken;
    }
}
