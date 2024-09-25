package backend.budget.jwt;

import backend.budget.auth.service.AuthService;
import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request); // JWT 토큰
        String requestURI = request.getRequestURI(); // 요청 URI

        String path = request.getRequestURI();
        if (path.equals("/api/signup") || path.equals("/api/signin")) {
            filterChain.doFilter(request, response);  // 로그인, 회원가입 요청은 건너뛰기
            return;
        }

        if(StringUtils.hasText(token) && jwtTokenProvider.validateCredential(token) ){
            log.info("Token is valid, attempting to get authentication for token: {}", token);
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            log.info("Authentication object: {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug(String.format("Security Context에 %s 인증 정보를 저장했습니다. URI : %s", authentication.getName(), requestURI));
        } else {
                throw new GeneralException(ErrorCode.EXPIRED_JWT_TOKEN);
        }

        //다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }
}
