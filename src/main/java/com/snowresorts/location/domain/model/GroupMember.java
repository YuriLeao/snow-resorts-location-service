package com.snowresorts.location.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Membership of a user in a {@link Group}. */
public record GroupMember(UUID groupId, UUID userId, Instant joinedAt) {
}
