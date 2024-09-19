package backend.budget.jwt;

import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
public class JwtTokenFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String token = resolveToken(httpRequest); // JWT 토큰
        String requestURI = httpRequest.getRequestURI(); // 요청 URI

        if(StringUtils.hasText(token) && jwtTokenProvider.validateCredential(token)){
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug(String.format("Security Context에 %s 인증 정보를 저장했습니다. URI : %s", authentication.getName(), requestURI));
        } else{
            throw new GeneralException(ErrorCode.INVALID_AUTH_TOKEN);
        }

        //다음 필터로 넘기기
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) { // 헤더에 'Bearer' 여부 확인
            return bearerToken.substring(7); // Bearer 제외 토큰 값 반환
        }
        return null;
    }
}
