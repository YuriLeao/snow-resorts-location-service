package com.snowresorts.location.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationSnapshotJpaRepository
        extends JpaRepository<LocationSnapshotEntity, UUID> {

    /** The most recent snapshot per user among the given user ids. */
    @Query("""
            select s from LocationSnapshotEntity s
            where s.userId in :userIds
              and s.recordedAt = (
                    select max(s2.recordedAt) from LocationSnapshotEntity s2
                    where s2.userId = s.userId)
            """)
    List<LocationSnapshotEntity> findLatestByUserIds(@Param("userIds") Collection<UUID> userIds);
}
