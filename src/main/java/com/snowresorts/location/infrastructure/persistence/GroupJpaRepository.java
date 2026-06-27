package com.snowresorts.location.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, UUID> {

    Optional<GroupEntity> findByInviteCode(String inviteCode);
}
