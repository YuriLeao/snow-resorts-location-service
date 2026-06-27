package com.snowresorts.location.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for the short-lived avatar URL cache (Redis key {@code avatar:{userId}},
 * TTL ~5min). MVP reads only from cache and never calls user-service synchronously.
 */
public interface AvatarCache {

    Optional<String> get(UUID userId);

    void put(UUID userId, String avatarUrl);
}
