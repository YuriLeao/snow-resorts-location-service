package com.snowresorts.location.infrastructure.persistence;

import com.snowresorts.location.domain.model.LocationSnapshot;
import com.snowresorts.location.domain.port.LocationSnapshots;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class LocationSnapshotRepositoryAdapter implements LocationSnapshots {

    private final LocationSnapshotJpaRepository jpaRepository;

    public LocationSnapshotRepositoryAdapter(LocationSnapshotJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LocationSnapshot save(LocationSnapshot snapshot) {
        LocationSnapshotEntity entity = new LocationSnapshotEntity(
                snapshot.id(), snapshot.userId(), snapshot.resortId(), snapshot.lat(),
                snapshot.lng(), snapshot.trailId(), snapshot.recordedAt());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<LocationSnapshot> findLatestByUserIds(Collection<UUID> userIds) {
        if (userIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findLatestByUserIds(userIds).stream()
                .map(this::toDomain)
                .toList();
    }

    private LocationSnapshot toDomain(LocationSnapshotEntity entity) {
        return new LocationSnapshot(entity.getId(), entity.getUserId(), entity.getResortId(),
                entity.getLat(), entity.getLng(), entity.getTrailId(), entity.getRecordedAt());
    }
}
