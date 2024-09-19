package backend.budget.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtTokenProvider (
            @Value("{JWT_SECRET_KEY") String secret,
            @Value("{access-expiration-time}") long accessExpirationTime,
            @Value("{refresh-expiration-time}") long refreshExpirationTime
    ){
        // 시크릿 값을 복호화하여 SecretKey 생성
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    @Transactional(readOnly = true)
    public String createAccessToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
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
                    .setSigningKey(secretKey.getEncoded())  // 서명 검증에 사용할 시크릿 키 설정
                    .build()
                    .parseClaimsJws(token)
                    .getBody();  // 서명 검증
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info(String.format("잘못된 JWT 서명입니다."));
        } catch (ExpiredJwtException e) {
            log.info(String.format("만료된 JWT 토큰입니다."));
        } catch (UnsupportedJwtException e) {
            log.info(String.format("지원되지 않는 JWT 토큰 입니다."));
        } catch (IllegalArgumentException e) {
            log.info(String.format("JWT 토큰이 잘못되었습니다."));
            e.printStackTrace();
        }

        return false;
    }
}
