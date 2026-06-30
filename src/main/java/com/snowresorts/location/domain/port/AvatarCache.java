package com.snowresorts.location.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for the short-lived avatar URL cache (Redis key {@code avatar:{userId}},
 * TTL ~5min). MVP reads only from cache and never calls user-service synchronously.
 * Entries are populated out of band (e.g. by user-service); on a miss the position
 * payload carries a {@code null} avatarUrl and the mobile client renders initials.
 */
public interface AvatarCache {

    Optional<String> get(UUID userId);
}
