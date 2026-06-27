package com.snowresorts.location.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "location", name = "groups")
public class GroupEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "resort_id")
    private UUID resortId;

    @Column(nullable = false)
    private String name;

    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected GroupEntity() {
    }

    public GroupEntity(UUID id, UUID resortId, String name, String inviteCode, UUID createdBy,
                       Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.resortId = resortId;
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getResortId() {
        return resortId;
    }

    public String getName() {
        return name;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
