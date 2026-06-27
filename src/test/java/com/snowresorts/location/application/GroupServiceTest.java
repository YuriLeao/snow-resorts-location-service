package com.snowresorts.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowresorts.location.domain.model.Group;
import com.snowresorts.location.domain.model.GroupDetails;
import com.snowresorts.location.domain.model.GroupMember;
import com.snowresorts.location.domain.port.GroupMembers;
import com.snowresorts.location.domain.port.Groups;
import com.snowresorts.security.error.ForbiddenException;
import com.snowresorts.security.error.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    private static final UUID CREATOR = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private Groups groups;
    @Mock
    private GroupMembers groupMembers;

    @org.mockito.InjectMocks
    private GroupService service;

    private Group sampleGroup(UUID id, String inviteCode) {
        return new Group(id, null, "Powder Hounds", inviteCode, CREATOR, null, Instant.now());
    }

    @Test
    @DisplayName("createGroup generates an invite code, persists the group and auto-joins the creator")
    void createGroup_always_generatesInviteCodeAndAddsCreatorAsMember() {
        // Arrange
        when(groups.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(groupMembers.findByGroupId(any())).thenReturn(
                List.of(new GroupMember(UUID.randomUUID(), CREATOR, Instant.now())));

        // Act
        GroupDetails details = service.createGroup(CREATOR, "Powder Hounds", null);

        // Assert
        ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
        verify(groups).save(groupCaptor.capture());
        Group persisted = groupCaptor.getValue();
        assertThat(persisted.name()).isEqualTo("Powder Hounds");
        assertThat(persisted.createdBy()).isEqualTo(CREATOR);
        assertThat(persisted.inviteCode()).isNotBlank();

        verify(groupMembers).add(persisted.id(), CREATOR);
        assertThat(details.members()).hasSize(1);
    }

    @Test
    @DisplayName("joinGroup with a valid invite code adds the caller as a member")
    void joinGroup_withValidCode_addsMembership() {
        // Arrange
        UUID groupId = UUID.randomUUID();
        Group group = sampleGroup(groupId, "ABCD2345");
        when(groups.findByInviteCode("ABCD2345")).thenReturn(Optional.of(group));
        when(groupMembers.exists(groupId, OTHER)).thenReturn(false);
        when(groupMembers.findByGroupId(groupId)).thenReturn(
                List.of(new GroupMember(groupId, OTHER, Instant.now())));

        // Act
        GroupDetails details = service.joinGroup(OTHER, "ABCD2345");

        // Assert
        verify(groupMembers).add(groupId, OTHER);
        assertThat(details.group().id()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("joinGroup with an unknown invite code throws ResourceNotFoundException")
    void joinGroup_withUnknownCode_throwsResourceNotFound() {
        when(groups.findByInviteCode("NOPE0000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.joinGroup(OTHER, "NOPE0000"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(groupMembers, never()).add(any(), any());
    }

    @Test
    @DisplayName("getGroup by a non-member throws ForbiddenException (IDOR protection)")
    void getGroup_byNonMember_throwsForbidden() {
        // Arrange
        UUID groupId = UUID.randomUUID();
        when(groups.findById(groupId)).thenReturn(Optional.of(sampleGroup(groupId, "ABCD2345")));
        when(groupMembers.exists(groupId, OTHER)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> service.getGroup(OTHER, groupId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("getGroup by a member returns the group with its members")
    void getGroup_byMember_returnsDetails() {
        // Arrange
        UUID groupId = UUID.randomUUID();
        when(groups.findById(groupId)).thenReturn(Optional.of(sampleGroup(groupId, "ABCD2345")));
        when(groupMembers.exists(groupId, CREATOR)).thenReturn(true);
        when(groupMembers.findByGroupId(groupId)).thenReturn(
                List.of(new GroupMember(groupId, CREATOR, Instant.now())));

        // Act
        GroupDetails details = service.getGroup(CREATOR, groupId);

        // Assert
        assertThat(details.group().id()).isEqualTo(groupId);
        assertThat(details.members()).hasSize(1);
        verify(groups).findById(eq(groupId));
    }
}
