package backend.budget.auth.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@RedisHash(value = "blackList")
public class BlackList {
    @Id
    private String id; //accessToken

    private String refreshToken;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long expiration;


    public BlackList(String accessToken, String refreshToken, Long expiration) {
        this.id = accessToken;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
