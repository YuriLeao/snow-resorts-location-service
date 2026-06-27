package com.snowresorts.location.infrastructure.persistence;

import com.snowresorts.location.domain.model.GroupMember;
import com.snowresorts.location.domain.port.GroupMembers;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class GroupMemberRepositoryAdapter implements GroupMembers {

    private final GroupMemberJpaRepository jpaRepository;

    public GroupMemberRepositoryAdapter(GroupMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public GroupMember add(UUID groupId, UUID userId) {
        GroupMemberEntity entity = new GroupMemberEntity(groupId, userId, Instant.now());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<GroupMember> findByGroupId(UUID groupId) {
        return jpaRepository.findByGroupId(groupId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean exists(UUID groupId, UUID userId) {
        return jpaRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    private GroupMember toDomain(GroupMemberEntity entity) {
        return new GroupMember(entity.getGroupId(), entity.getUserId(), entity.getJoinedAt());
    }
}
