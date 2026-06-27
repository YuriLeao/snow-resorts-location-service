package com.snowresorts.location.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "location", name = "group_members")
@IdClass(GroupMemberId.class)
public class GroupMemberEntity {

    @Id
    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected GroupMemberEntity() {
    }

    public GroupMemberEntity(UUID groupId, UUID userId, Instant joinedAt) {
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}
