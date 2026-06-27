package com.snowresorts.location.infrastructure.web;

import com.snowresorts.location.domain.model.GroupMember;
import java.time.Instant;
import java.util.UUID;

public record GroupMemberResponse(UUID userId, Instant joinedAt) {

    static GroupMemberResponse from(GroupMember member) {
        return new GroupMemberResponse(member.userId(), member.joinedAt());
    }
}
