package com.snowresorts.location.domain.port;

import com.snowresorts.location.domain.model.Group;
import java.util.Optional;
import java.util.UUID;

/** Outbound port for group persistence. */
public interface Groups {

    Group save(Group group);

    Optional<Group> findById(UUID id);

    Optional<Group> findByInviteCode(String inviteCode);
}
