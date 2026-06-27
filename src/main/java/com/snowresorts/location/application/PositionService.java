package com.snowresorts.location.application;

import com.snowresorts.location.domain.model.FriendPosition;
import com.snowresorts.location.domain.model.LocationSnapshot;
import com.snowresorts.location.domain.model.PositionUpdate;
import com.snowresorts.location.domain.port.AvatarCache;
import com.snowresorts.location.domain.port.LocationSnapshots;
import com.snowresorts.location.domain.port.PositionBroadcaster;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists incoming positions and broadcasts them to a group, enriching each
 * {@link FriendPosition} with the rider's avatar URL from the short-lived cache.
 */
@Service
public class PositionService {

    private static final Logger log = LoggerFactory.getLogger(PositionService.class);

    private final LocationSnapshots snapshots;
    private final PositionBroadcaster broadcaster;
    private final AvatarCache avatarCache;

    public PositionService(LocationSnapshots snapshots, PositionBroadcaster broadcaster,
                           AvatarCache avatarCache) {
        this.snapshots = snapshots;
        this.broadcaster = broadcaster;
        this.avatarCache = avatarCache;
    }

    /**
     * Records a position for {@code userId}: persists a snapshot, builds an avatar-enriched
     * {@link FriendPosition} and fans it out to the group's Redis channel.
     *
     * @return the broadcast position (also returned to the publisher for convenience)
     */
    @Transactional
    public FriendPosition recordPosition(UUID groupId, UUID userId, PositionUpdate update) {
        Instant recordedAt = Instant.now();
        snapshots.save(new LocationSnapshot(
                UUID.randomUUID(), userId, null, update.lat(), update.lng(),
                update.trailId(), recordedAt));

        String avatarUrl = avatarCache.get(userId).orElse(null);
        FriendPosition position = new FriendPosition(
                userId, update.lat(), update.lng(), update.trailId(),
                update.speedKmh(), avatarUrl, recordedAt);

        broadcaster.publish(groupId, position);
        log.debug("Recorded + broadcast position for user {} in group {}", userId, groupId);
        return position;
    }

    /** Reads the latest known snapshot per member as avatar-enriched {@link FriendPosition}s. */
    @Transactional(readOnly = true)
    public List<FriendPosition> latestPositionsFor(Collection<UUID> userIds) {
        return snapshots.findLatestByUserIds(userIds).stream()
                .map(this::toFriendPosition)
                .toList();
    }

    private FriendPosition toFriendPosition(LocationSnapshot snapshot) {
        String avatarUrl = avatarCache.get(snapshot.userId()).orElse(null);
        return new FriendPosition(
                snapshot.userId(), snapshot.lat(), snapshot.lng(), snapshot.trailId(),
                0.0, avatarUrl, snapshot.recordedAt());
    }
}
