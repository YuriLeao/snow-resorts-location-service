package com.snowresorts.location.infrastructure.web;

import com.snowresorts.location.domain.model.GroupDetails;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        UUID resortId,
        String name,
        String inviteCode,
        UUID createdBy,
        Instant expiresAt,
        Instant createdAt,
        List<GroupMemberResponse> members) {

    static GroupResponse from(GroupDetails details) {
        List<GroupMemberResponse> members = details.members().stream()
                .map(GroupMemberResponse::from)
                .toList();
        return new GroupResponse(
                details.group().id(),
                details.group().resortId(),
                details.group().name(),
                details.group().inviteCode(),
                details.group().createdBy(),
                details.group().expiresAt(),
                details.group().createdAt(),
                members);
    }
}
