package com.snowresorts.location.domain.model;

import java.time.Instant;
import java.util.UUID;

/** A ski group that friends join via an invite code to see each other on the map. */
public record Group(
        UUID id,
        UUID resortId,
        String name,
        String inviteCode,
        UUID createdBy,
        Instant expiresAt,
        Instant createdAt) {
}
