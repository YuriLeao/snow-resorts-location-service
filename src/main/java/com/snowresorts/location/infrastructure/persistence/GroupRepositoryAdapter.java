package com.snowresorts.location.infrastructure.persistence;

import com.snowresorts.location.domain.model.Group;
import com.snowresorts.location.domain.port.Groups;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepositoryAdapter implements Groups {

    private final GroupJpaRepository jpaRepository;

    public GroupRepositoryAdapter(GroupJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Group save(Group group) {
        GroupEntity entity = new GroupEntity(
                group.id(), group.resortId(), group.name(), group.inviteCode(),
                group.createdBy(), group.expiresAt(), group.createdAt());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Group> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Group> findByInviteCode(String inviteCode) {
        return jpaRepository.findByInviteCode(inviteCode).map(this::toDomain);
    }

    private Group toDomain(GroupEntity entity) {
        return new Group(entity.getId(), entity.getResortId(), entity.getName(),
                entity.getInviteCode(), entity.getCreatedBy(), entity.getExpiresAt(),
                entity.getCreatedAt());
    }
}
