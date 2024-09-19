package backend.budget.auth.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 86400) // 24시간 (60 * 60 * 24)
public class RefreshToken {
    @Id
    private String refreshToken;

    @Indexed
    private String accessToken;

    @Indexed
    private String userName;

    private LocalDateTime createdAt;

    public RefreshToken(String accessToken, String refreshToken, String userName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.createdAt = LocalDateTime.now();
    }

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
