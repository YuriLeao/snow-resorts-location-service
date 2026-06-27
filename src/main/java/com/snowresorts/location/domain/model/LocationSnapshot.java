package com.snowresorts.location.domain.model;

import java.time.Instant;
import java.util.UUID;

/** A point-in-time GPS position persisted for a user (TTL 24h, pruned out of band). */
public record LocationSnapshot(
        UUID id,
        UUID userId,
        UUID resortId,
        double lat,
        double lng,
        UUID trailId,
        Instant recordedAt) {
}
