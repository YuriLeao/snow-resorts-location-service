package com.snowresorts.location.domain.port;

import com.snowresorts.location.domain.model.FriendPosition;
import java.util.UUID;

/**
 * Outbound port that fans a {@link FriendPosition} out to every service instance so all
 * subscribers of a group receive it. The Redis adapter publishes to {@code location.group.{id}}.
 */
public interface PositionBroadcaster {

    void publish(UUID groupId, FriendPosition position);
}
