package com.snowresorts.location.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "location", name = "location_snapshots")
public class LocationSnapshotEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "resort_id")
    private UUID resortId;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(name = "trail_id")
    private UUID trailId;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected LocationSnapshotEntity() {
    }

    public LocationSnapshotEntity(UUID id, UUID userId, UUID resortId, double lat, double lng,
                                  UUID trailId, Instant recordedAt) {
        this.id = id;
        this.userId = userId;
        this.resortId = resortId;
        this.lat = lat;
        this.lng = lng;
        this.trailId = trailId;
        this.recordedAt = recordedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getResortId() {
        return resortId;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public UUID getTrailId() {
        return trailId;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
