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
    private String userName; //userName

    @Indexed
    private String refreshToken;

    private LocalDateTime createdAt;

    public RefreshToken(String userName, String refreshToken) {
        this.userName = userName;
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
    }
}
