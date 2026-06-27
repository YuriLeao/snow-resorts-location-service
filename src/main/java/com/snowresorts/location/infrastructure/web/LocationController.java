package com.snowresorts.location.infrastructure.web;

import com.snowresorts.location.application.GroupService;
import com.snowresorts.location.application.PositionService;
import com.snowresorts.location.domain.model.FriendPosition;
import com.snowresorts.location.domain.model.GroupMember;
import com.snowresorts.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST surface for groups and the latest known position of each member. */
@RestController
@RequestMapping("/snow-resort-service/v1/location")
public class LocationController {

    private final GroupService groupService;
    private final PositionService positionService;

    public LocationController(GroupService groupService, PositionService positionService) {
        this.groupService = groupService;
        this.positionService = positionService;
    }

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        UUID currentUserId = SecurityUtils.requireCurrentUserId();
        return GroupResponse.from(
                groupService.createGroup(currentUserId, request.name(), request.resortId()));
    }

    @PostMapping("/groups/{inviteCode}/join")
    public GroupResponse joinGroup(@PathVariable String inviteCode) {
        UUID currentUserId = SecurityUtils.requireCurrentUserId();
        return GroupResponse.from(groupService.joinGroup(currentUserId, inviteCode));
    }

    @GetMapping("/groups/{id}")
    public GroupResponse getGroup(@PathVariable UUID id) {
        UUID currentUserId = SecurityUtils.requireCurrentUserId();
        return GroupResponse.from(groupService.getGroup(currentUserId, id));
    }

    @GetMapping("/groups/{id}/positions")
    public List<FriendPosition> getPositions(@PathVariable UUID id) {
        UUID currentUserId = SecurityUtils.requireCurrentUserId();
        List<UUID> memberIds = groupService.requireMembers(currentUserId, id).stream()
                .map(GroupMember::userId)
                .toList();
        return positionService.latestPositionsFor(memberIds);
    }
}
