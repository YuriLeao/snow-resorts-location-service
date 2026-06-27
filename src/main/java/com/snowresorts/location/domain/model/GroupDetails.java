package com.snowresorts.location.domain.model;

import java.util.List;

/** A {@link Group} together with its current members. */
public record GroupDetails(Group group, List<GroupMember> members) {
}
