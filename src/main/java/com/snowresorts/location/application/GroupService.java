package com.snowresorts.location.application;

import com.snowresorts.location.domain.model.Group;
import com.snowresorts.location.domain.model.GroupDetails;
import com.snowresorts.location.domain.model.GroupMember;
import com.snowresorts.location.domain.port.GroupMembers;
import com.snowresorts.location.domain.port.Groups;
import com.snowresorts.security.error.ForbiddenException;
import com.snowresorts.security.error.ResourceNotFoundException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Group lifecycle and membership: creation (with auto-join of the creator), join-by-invite,
 * and member-only reads. All reads enforce membership to prevent IDOR.
 */
@Service
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);
    private static final char[] INVITE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final int INVITE_CODE_LENGTH = 8;

    private final Groups groups;
    private final GroupMembers groupMembers;
    private final SecureRandom random = new SecureRandom();

    public GroupService(Groups groups, GroupMembers groupMembers) {
        this.groups = groups;
        this.groupMembers = groupMembers;
    }

    @Transactional
    public GroupDetails createGroup(UUID currentUserId, String name, UUID resortId) {
        Group group = new Group(
                UUID.randomUUID(), resortId, name, generateInviteCode(),
                currentUserId, null, Instant.now());
        Group saved = groups.save(group);
        groupMembers.add(saved.id(), currentUserId);
        log.debug("Created group {} ({}) for user {}", saved.id(), saved.inviteCode(), currentUserId);
        return new GroupDetails(saved, groupMembers.findByGroupId(saved.id()));
    }

    @Transactional
    public GroupDetails joinGroup(UUID currentUserId, String inviteCode) {
        Group group = groups.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No group found for invite code '%s'.".formatted(inviteCode)));
        if (!groupMembers.exists(group.id(), currentUserId)) {
            groupMembers.add(group.id(), currentUserId);
            log.debug("User {} joined group {}", currentUserId, group.id());
        }
        return new GroupDetails(group, groupMembers.findByGroupId(group.id()));
    }

    @Transactional(readOnly = true)
    public GroupDetails getGroup(UUID currentUserId, UUID groupId) {
        Group group = requireGroup(groupId);
        requireMembership(currentUserId, group.id());
        return new GroupDetails(group, groupMembers.findByGroupId(group.id()));
    }

    /**
     * Verifies the caller is a member of the group and returns the member list. Used by the
     * positions endpoint to scope the read to the caller's own group.
     */
    @Transactional(readOnly = true)
    public List<GroupMember> requireMembers(UUID currentUserId, UUID groupId) {
        requireGroup(groupId);
        requireMembership(currentUserId, groupId);
        return groupMembers.findByGroupId(groupId);
    }

    private Group requireGroup(UUID groupId) {
        return groups.findById(groupId)
                .orElseThrow(() -> ResourceNotFoundException.of("Group", groupId));
    }

    private void requireMembership(UUID currentUserId, UUID groupId) {
        if (!groupMembers.exists(groupId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this group.");
        }
    }

    private String generateInviteCode() {
        StringBuilder code = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            code.append(INVITE_ALPHABET[random.nextInt(INVITE_ALPHABET.length)]);
        }
        return code.toString();
    }
}
