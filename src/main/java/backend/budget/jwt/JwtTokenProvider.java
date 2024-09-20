package backend.budget.jwt;

import backend.budget.common.constants.ErrorCode;
import backend.budget.common.exceptions.GeneralException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtTokenProvider (
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token.access-expiration-time}") long accessExpirationTime,
            @Value("${jwt.token.refresh-expiration-time}") long refreshExpirationTime
    ){
        // 시크릿 값을 복호화하여 SecretKey 생성
        try{
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (GeneralException e) {
            log.error("시크릿 키 생성 중 오류가 발생했습니다: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INVALID_SECRET_KEY);
        }

        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    @Transactional(readOnly = true)
    public String createAccessToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS512) // HMAC + SHA512
                .compact();
    }

    @Transactional(readOnly = true)
    public String createRefreshToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS512) // HMAC + SHA512
                .compact();
    }

    @Transactional(readOnly = true)
    public boolean validateCredential(String token){
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)  // 서명 검증에 사용할 시크릿 키 설정
                    .build()
                    .parseClaimsJws(token)
                    .getBody();  // 서명 검증
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new GeneralException(ErrorCode.SIGNATURE_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info(String.format("만료된 JWT 토큰입니다."));
            throw new GeneralException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info(String.format("지원되지 않는 JWT 토큰 입니다."));
            throw new GeneralException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info(String.format("JWT 토큰이 잘못되었습니다."));
            e.printStackTrace();
        }

        return false;
    }

    @Transactional(readOnly = true)
    public String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) { // 헤더에 'Bearer' 여부 확인
            return bearerToken.substring(7); // Bearer 제외 토큰 값 반환
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("RefreshToken");
    }

    @Transactional(readOnly = true)
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();

        String username = claims.getSubject();
        String roles = claims.get("roles", String.class);  // 권한 정보 추출

        // 권한 정보를 GrantedAuthority 리스트로 변환
        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 사용자 이름과 권한을 포함한 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    @Transactional(readOnly = true)
    public Long getExpiration(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        long now = new Date().getTime();

        return expiration.getTime() - now;
    }
}
