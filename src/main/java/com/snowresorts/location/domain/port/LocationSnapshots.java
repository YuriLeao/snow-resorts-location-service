package com.snowresorts.location.domain.port;

import com.snowresorts.location.domain.model.LocationSnapshot;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** Outbound port for location-snapshot persistence and "latest per user" reads. */
public interface LocationSnapshots {

    LocationSnapshot save(LocationSnapshot snapshot);

    /** @return the most recent snapshot for each of the given users (at most one per user). */
    List<LocationSnapshot> findLatestByUserIds(Collection<UUID> userIds);
}
