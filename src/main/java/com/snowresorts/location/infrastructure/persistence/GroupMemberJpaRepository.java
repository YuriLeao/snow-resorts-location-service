package com.snowresorts.location.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberJpaRepository
        extends JpaRepository<GroupMemberEntity, GroupMemberId> {

    List<GroupMemberEntity> findByGroupId(UUID groupId);

    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
}
