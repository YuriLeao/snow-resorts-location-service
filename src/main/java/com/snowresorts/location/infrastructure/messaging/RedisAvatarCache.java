package com.snowresorts.location.infrastructure.messaging;

import com.snowresorts.location.domain.port.AvatarCache;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis adapter for {@link AvatarCache}. Stores avatar URLs at {@code avatar:{userId}} with a
 * short TTL. The cache is populated out of band (e.g. by user-service); on a miss the position
 * payload carries a {@code null} avatarUrl and the mobile client renders initials.
 */
@Component
public class RedisAvatarCache implements AvatarCache {

    static final String KEY_PREFIX = "avatar:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public RedisAvatarCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<String> get(UUID userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + userId));
    }

    @Override
    public void put(UUID userId, String avatarUrl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, avatarUrl, TTL);
    }
}
