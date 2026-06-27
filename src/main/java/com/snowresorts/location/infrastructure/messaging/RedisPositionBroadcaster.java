package com.snowresorts.location.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snowresorts.location.domain.model.FriendPosition;
import com.snowresorts.location.domain.port.PositionBroadcaster;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub adapter for {@link PositionBroadcaster}. Publishing emits the JSON of a
 * {@link FriendPosition} to {@code location.group.{groupId}}. As a {@link MessageListener}
 * (subscribed to the {@code location.group.*} pattern) it receives messages from ALL service
 * instances and forwards them to STOMP {@code /topic/groups/{groupId}}, giving every connected
 * client a consistent broadcast regardless of which instance handled the original update.
 */
@Component
public class RedisPositionBroadcaster implements PositionBroadcaster, MessageListener {

    static final String CHANNEL_PREFIX = "location.group.";
    static final String CHANNEL_PATTERN = CHANNEL_PREFIX + "*";

    private static final Logger log = LoggerFactory.getLogger(RedisPositionBroadcaster.class);

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisPositionBroadcaster(StringRedisTemplate redisTemplate,
                                    SimpMessagingTemplate messagingTemplate,
                                    ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(UUID groupId, FriendPosition position) {
        try {
            String payload = objectMapper.writeValueAsString(position);
            redisTemplate.convertAndSend(CHANNEL_PREFIX + groupId, payload);
        } catch (Exception ex) {
            log.error("Failed to publish position for group {}", groupId, ex);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String groupId = channel.substring(CHANNEL_PREFIX.length());
        try {
            FriendPosition position = objectMapper.readValue(
                    message.getBody(), FriendPosition.class);
            messagingTemplate.convertAndSend("/topic/groups/" + groupId, position);
        } catch (Exception ex) {
            log.error("Failed to forward position from channel {}", channel, ex);
        }
    }
}
