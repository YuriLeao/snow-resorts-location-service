package com.snowresorts.location.infrastructure.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Wires the Redis Pub/Sub listener container that drives cross-instance fanout. The
 * {@link RedisPositionBroadcaster} subscribes to the {@code location.group.*} pattern; on each
 * message it relays the position to the local STOMP broker. These beans live only in the normal
 * application context (the integration test supplies a Redis container).
 */
@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory, RedisPositionBroadcaster broadcaster) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(broadcaster,
                new PatternTopic(RedisPositionBroadcaster.CHANNEL_PATTERN));
        return container;
    }
}
