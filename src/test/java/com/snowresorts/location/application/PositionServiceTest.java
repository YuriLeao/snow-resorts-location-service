package com.snowresorts.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowresorts.location.domain.model.FriendPosition;
import com.snowresorts.location.domain.model.LocationSnapshot;
import com.snowresorts.location.domain.model.PositionUpdate;
import com.snowresorts.location.domain.port.AvatarCache;
import com.snowresorts.location.domain.port.LocationSnapshots;
import com.snowresorts.location.domain.port.PositionBroadcaster;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    private static final UUID GROUP_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID USER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Mock
    private LocationSnapshots snapshots;
    @Mock
    private PositionBroadcaster broadcaster;
    @Mock
    private AvatarCache avatarCache;

    @org.mockito.InjectMocks
    private PositionService service;

    @Test
    @DisplayName("recordPosition persists a snapshot and broadcasts a FriendPosition with cached avatar")
    void recordPosition_withCachedAvatar_persistsAndBroadcastsEnrichedPosition() {
        // Arrange
        PositionUpdate update = new PositionUpdate(45.92, 6.87, null, 42.5);
        when(avatarCache.get(USER_ID)).thenReturn(Optional.of("https://cdn/avatar/u44.webp"));

        // Act
        FriendPosition result = service.recordPosition(GROUP_ID, USER_ID, update);

        // Assert
        ArgumentCaptor<LocationSnapshot> snapshotCaptor = ArgumentCaptor.forClass(LocationSnapshot.class);
        verify(snapshots).save(snapshotCaptor.capture());
        LocationSnapshot saved = snapshotCaptor.getValue();
        assertThat(saved.userId()).isEqualTo(USER_ID);
        assertThat(saved.lat()).isEqualTo(45.92);
        assertThat(saved.lng()).isEqualTo(6.87);

        ArgumentCaptor<FriendPosition> positionCaptor = ArgumentCaptor.forClass(FriendPosition.class);
        verify(broadcaster).publish(eq(GROUP_ID), positionCaptor.capture());
        FriendPosition broadcast = positionCaptor.getValue();
        assertThat(broadcast.userId()).isEqualTo(USER_ID);
        assertThat(broadcast.speedKmh()).isEqualTo(42.5);
        assertThat(broadcast.avatarUrl()).isEqualTo("https://cdn/avatar/u44.webp");
        assertThat(result.avatarUrl()).isEqualTo("https://cdn/avatar/u44.webp");
    }

    @Test
    @DisplayName("recordPosition leaves avatarUrl null when the avatar cache misses")
    void recordPosition_withCacheMiss_broadcastsNullAvatarUrl() {
        // Arrange
        PositionUpdate update = new PositionUpdate(45.92, 6.87, UUID.randomUUID(), 12.0);
        when(avatarCache.get(USER_ID)).thenReturn(Optional.empty());

        // Act
        FriendPosition result = service.recordPosition(GROUP_ID, USER_ID, update);

        // Assert
        verify(snapshots).save(any(LocationSnapshot.class));
        ArgumentCaptor<FriendPosition> positionCaptor = ArgumentCaptor.forClass(FriendPosition.class);
        verify(broadcaster).publish(eq(GROUP_ID), positionCaptor.capture());
        assertThat(positionCaptor.getValue().avatarUrl()).isNull();
        assertThat(result.avatarUrl()).isNull();
    }
}
