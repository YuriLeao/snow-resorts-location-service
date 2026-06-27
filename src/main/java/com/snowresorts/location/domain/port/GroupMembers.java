package com.snowresorts.location.domain.port;

import com.snowresorts.location.domain.model.GroupMember;
import java.util.List;
import java.util.UUID;

/** Outbound port for group-membership persistence. */
public interface GroupMembers {

    GroupMember add(UUID groupId, UUID userId);

    List<GroupMember> findByGroupId(UUID groupId);

    boolean exists(UUID groupId, UUID userId);
}
