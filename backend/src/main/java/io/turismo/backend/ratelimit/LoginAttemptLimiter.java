package io.turismo.backend.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "ratelimit.enabled", matchIfMissing = true)
public class LoginAttemptLimiter {
    private static final DefaultRedisScript<Long> RESERVE = new DefaultRedisScript<>("""
            local clock = redis.call('TIME')
            local now = tonumber(clock[1]) * 1000 + math.floor(tonumber(clock[2]) / 1000)
            local window = 900000
            for i = 1, 2 do
                redis.call('ZREMRANGEBYSCORE', KEYS[i], '-inf', now - window)
            end
            local wait = 0
            local limits = {tonumber(ARGV[2]), tonumber(ARGV[3])}
            for i = 1, 2 do
                local count = redis.call('ZCARD', KEYS[i])
                if count >= limits[i] then
                    local index = count - limits[i]
                    local entry = redis.call('ZRANGE', KEYS[i], index, index, 'WITHSCORES')
                    wait = math.max(wait, tonumber(entry[2]) + window - now)
                end
            end
            if wait > 0 then return wait end
            for i = 1, 2 do
                redis.call('ZADD', KEYS[i], now, ARGV[1])
                redis.call('PEXPIRE', KEYS[i], window)
            end
            return 0
            """, Long.class);

    private final StringRedisTemplate redis;

    private final String accountCapacity;
    private final String ipCapacity;

    public LoginAttemptLimiter(StringRedisTemplate redis,
                               @Value("${ratelimit.login.capacity:5}") long accountCapacity,
                               @Value("${ratelimit.login.ip-capacity:30}") long ipCapacity) {
        if (accountCapacity < 1 || ipCapacity < 1) {
            throw new IllegalArgumentException("Os limites de login devem ser positivos");
        }
        this.redis = redis;
        this.accountCapacity = Long.toString(accountCapacity);
        this.ipCapacity = Long.toString(ipCapacity);
    }

    public Reservation reserve(String email, String ip) {
        String accountKey = "turismo:ratelimit:login:account:" + hash(Objects.toString(email, ""));
        String ipKey = "turismo:ratelimit:login:ip:" + ip;
        String attemptId = UUID.randomUUID().toString();
        Long wait = redis.execute(RESERVE, List.of(accountKey, ipKey), attemptId, accountCapacity, ipCapacity);
        if (wait == null) {
            throw new IllegalStateException("Não foi possível verificar o limite de login");
        }
        return new Reservation(accountKey, attemptId, wait);
    }

    public void completeSuccessfulLogin(Reservation reservation) {
        redis.opsForZSet().remove(reservation.accountKey(), reservation.attemptId());
    }

    private String hash(String email) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(email.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível", exception);
        }
    }

    public record Reservation(String accountKey, String attemptId, long waitMillis) {}
}
