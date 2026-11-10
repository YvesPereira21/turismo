package io.turismo.backend.security;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "ratelimit.enabled", matchIfMissing = true)
public class PublicRateLimitService {

    enum Policy { LOGIN, REFRESH, REGISTRATION }

    private final LettuceConnectionFactory connectionFactory;
    private final Map<Policy, BucketConfiguration> policies;

    public PublicRateLimitService(LettuceConnectionFactory connectionFactory,
                                  @Value("${ratelimit.login.capacity:5}") long loginCapacity,
                                  @Value("${ratelimit.refresh.capacity:5}") long refreshCapacity,
                                  @Value("${ratelimit.registration.capacity:5}") long registrationCapacity) {
        this.connectionFactory = connectionFactory;
        this.policies = Map.of(
                Policy.LOGIN, configuration(loginCapacity, Duration.ofMinutes(1)),
                Policy.REFRESH, configuration(refreshCapacity, Duration.ofMinutes(1)),
                Policy.REGISTRATION, configuration(registrationCapacity, Duration.ofMinutes(15)));
    }

    ConsumptionProbe tryConsume(Policy policy, String ip) {
        byte[] key = ("turismo:ratelimit:v1:" + policy.name() + ":" + ip).getBytes(StandardCharsets.UTF_8);
        try (var connection = (LettuceConnection) connectionFactory.getConnection()) {
            var commands = (RedisAsyncCommands<byte[], byte[]>) connection.getNativeConnection();
            var manager = Bucket4jLettuce.casBasedBuilder(commands)
                    .requestTimeout(Duration.ofMillis(500))
                    .expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                            Duration.ofSeconds(10)))
                    .build();
            return manager.builder().build(key, () -> policies.get(policy)).tryConsumeAndReturnRemaining(1);
        }
    }

    private static BucketConfiguration configuration(long capacity, Duration period) {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(capacity).refillIntervally(capacity, period))
                .build();
    }
}
