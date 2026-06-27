package com.snowresorts.location.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound position broadcast to a group's subscribers, enriched with the rider's
 * {@code avatarUrl} (nullable; mobile falls back to initials when absent).
 */
public record FriendPosition(
        UUID userId,
        double lat,
        double lng,
        UUID trailId,
        double speedKmh,
        String avatarUrl,
        Instant recordedAt) {
}
