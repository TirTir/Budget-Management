package backend.budget.auth.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 604800) // 7일 (60 * 60 * 24 * 7)
public class RefreshToken {
    @Id
    private String refreshToken;

    @Indexed
    private String accessToken;

    private LocalDateTime createdAt;

    public RefreshToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
    }

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
