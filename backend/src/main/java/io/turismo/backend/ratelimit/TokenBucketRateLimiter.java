package io.turismo.backend.ratelimit;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@ConditionalOnProperty(name = "ratelimit.enabled", matchIfMissing = true)
public class TokenBucketRateLimiter {

    private final LettuceConnectionFactory connectionFactory;
    private StatefulRedisConnection<byte[], byte[]> connection;
    private ProxyManager<byte[]> proxyManager;

    public TokenBucketRateLimiter(LettuceConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public ConsumptionProbe consume(String key, BucketConfiguration configuration) {
        return proxyManager().builder()
                .build(key.getBytes(StandardCharsets.UTF_8), () -> configuration)
                .tryConsumeAndReturnRemaining(1);
    }

    private synchronized ProxyManager<byte[]> proxyManager() {
        if (proxyManager == null) {
            var client = (RedisClient) connectionFactory.getRequiredNativeClient();
            connection = client.connect(ByteArrayCodec.INSTANCE);
            proxyManager = Bucket4jLettuce.casBasedBuilder(connection)
                    .requestTimeout(Duration.ofMillis(500))
                    .expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                            Duration.ofSeconds(10)))
                    .build();
        }
        return proxyManager;
    }

    @PreDestroy
    synchronized void closeConnection() {
        if (connection != null) {
            connection.close();
        }
    }

}
