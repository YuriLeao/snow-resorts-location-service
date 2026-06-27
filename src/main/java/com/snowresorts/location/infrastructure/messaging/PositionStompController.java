package com.snowresorts.location.infrastructure.messaging;

import com.snowresorts.location.application.PositionService;
import com.snowresorts.location.domain.model.PositionUpdate;
import java.security.Principal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Receives STOMP position updates published to {@code /app/groups/{groupId}/position}.
 *
 * <p>MVP authentication: the rider's id is taken from the STOMP {@link Principal} name (the JWT
 * {@code sub}, set during the CONNECT handshake). When no principal is present (e.g. an anonymous
 * dev session) the update is ignored rather than persisted with an unknown identity.
 */
@Controller
public class PositionStompController {

    private static final Logger log = LoggerFactory.getLogger(PositionStompController.class);

    private final PositionService positionService;

    public PositionStompController(PositionService positionService) {
        this.positionService = positionService;
    }

    @MessageMapping("/groups/{groupId}/position")
    public void handlePosition(@DestinationVariable UUID groupId,
                               @Payload PositionUpdate update,
                               Principal principal) {
        if (principal == null || principal.getName() == null) {
            log.warn("Dropping position update for group {}: no authenticated principal", groupId);
            return;
        }
        UUID userId = UUID.fromString(principal.getName());
        positionService.recordPosition(groupId, userId, update);
    }
}
