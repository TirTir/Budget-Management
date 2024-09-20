package backend.budget.auth.service;

import backend.budget.auth.dto.AuthResponse;
import backend.budget.auth.dto.RefreshResponse;
import backend.budget.auth.entity.BlackList;
import backend.budget.auth.entity.RefreshToken;
import backend.budget.auth.repository.BlackListRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final BlackListRepository blackListRepository;

    @Transactional(readOnly = true)
    public AuthResponse getAuthToken(String userName, String password){
        log.info("Attempting authentication for user: {}", userName);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // RefreshToken 저장
        RefreshToken redis = new RefreshToken(userName, refreshToken);
        refreshTokenRepository.save(redis);
        log.info("RefreshToken saved ID: {}", userName);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshResponse getRefresh(String accessToken, String refreshToken){

        // redis 엔티티 조회
        RefreshToken redis = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (redis == null) {
            throw new GeneralException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // refreshToken 유효성 검사
        if (!jwtTokenProvider.validateCredential(refreshToken)) {
            throw new GeneralException(ErrorCode.INVALID_AUTH_TOKEN);
        }

        // 블랙리스트 확인
        BlackList blackList = blackListRepository.findById(refreshToken).orElse(null);
        if (blackList != null) {
            throw new GeneralException(ErrorCode.BLACKLISTED_TOKEN);  // 블랙리스트에 있는 토큰이면 예외 처리
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        return new RefreshResponse(newAccessToken);
    }

    @Transactional(readOnly = true)
    public void deleteAuthToken(String accessToken, String refreshToken){
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        refreshTokenRepository.deleteById(authentication.getName());

        // 블랙리스트 저장
        Long expiraion = jwtTokenProvider.getExpiration(refreshToken);
        BlackList blackList = new BlackList(accessToken, refreshToken, expiraion);
        blackListRepository.save(blackList);
    }
}
