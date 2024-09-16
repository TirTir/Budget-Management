package backend.budget.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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

    public String createAccessToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.name())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS512) // HMAC + SHA512
                .compact();
    }

    public String createRefreshToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
                .setSubject(authentication.name())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS512) // HMAC + SHA512
                .compact();
    }
}
